import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class RBT {
    public Node root = null;

    // required rotations and recolouring are performed while backtracking the recursive stack
    // these flags are required to pass the required actions one step backward in the recursive stack
    private boolean rotateLeft = false;
    private boolean rotateRight = false;
    private boolean rotateLeftThenRight = false;
    private boolean rotateRightThenLeft = false;

    static class Node {
        boolean isRed;
        int key;
        Node parent;
        Node left;
        Node right;

        Node(int key) {
            this.isRed = true;
            this.key = key;
            this.parent = null;
            this.left = null;
            this.right = null;
        }
    }

    private Node rotateLeft(Node node) {
        Node oldRight = node.right;
        Node oldLeft = oldRight.left;
        oldRight.left = node;
        node.right = oldLeft;
        node.parent = oldRight;
        if (oldLeft != null) {
            oldLeft.parent = node;
        }
        return (oldRight);
    }

    private Node rotateRight(Node node) {
        Node oldLeft = node.left;
        Node oldRight = oldLeft.right;
        oldLeft.right = node;
        node.left = oldRight;
        node.parent = oldLeft;
        if (oldRight != null) {
            oldRight.parent = node;
        }
        return (oldLeft);
    }

    private Node checkRequiredRotations(Node root) {
        if (this.rotateLeft) {
            root = rotateLeft(root);
            root.isRed = false;
            root.left.isRed = true;

            this.rotateLeft = false;
        } else if (this.rotateRight) {
            root = rotateRight(root);
            root.isRed = false;
            root.right.isRed = true;

            this.rotateRight = false;
        } else if (this.rotateRightThenLeft) {
            root.right = rotateRight(root.right);
            root.right.parent = root;
            root = rotateLeft(root);
            root.isRed = false;
            root.left.isRed = true;

            this.rotateRightThenLeft = false;
        } else if (this.rotateLeftThenRight) {
            root.left = rotateLeft(root.left);
            root.left.parent = root;
            root = rotateRight(root);
            root.isRed = false;
            root.right.isRed = true;

            this.rotateLeftThenRight = false;
        }
        return root;
    }

    /**
     * this will instruct certain rotation and recolouring if required.
     * it will be done while backtracking the recursive stack.
     * when rotation and recolouring is done the flags will be reset in checkRequiredRotations().
     *
     * @param root Node
     */
    private Node processRedRedConflict(Node root) {
        if (root.parent.right == root) {    // current node is the right child of its parent
            if (root.parent.left == null || !root.parent.left.isRed) {      // other child of the grandparent (left parent's sibling) is a black node / nil (algodat: case 2)
                if (root.left != null && root.left.isRed) {
                    this.rotateRightThenLeft = true;
                } else if (root.right != null && root.right.isRed) {
                    this.rotateLeft = true;
                }
            } else {    // other child of the grandparent (left parent's sibling) is a red node (algodat: case 1)
                root.parent.left.isRed = false;
                root.isRed = false;
                if (root.parent != this.root) {
                    root.parent.isRed = true;
                }
            }
        } else {    // current node is the left child of its parent
            if (root.parent.right == null || !root.parent.right.isRed) {    // other child of the grandparent (right parent's sibling) is a black node / nil (algodat: case 3)
                if (root.left != null && root.left.isRed) {
                    this.rotateRight = true;
                } else if (root.right != null && root.right.isRed) {
                    this.rotateLeftThenRight = true;
                }
            } else {    // other child of the grandparent (right parent's sibling) is a red node (algodat: case 1)
                root.parent.right.isRed = false;
                root.isRed = false;
                if (root.parent != this.root) {
                    root.parent.isRed = true;
                }
            }
        }
        return root;
    }

    /**
     * helper function for insertion.
     * this function performs all required tasks to insert a key in the tree and takes care of the BST properties.
     *
     * @param root Node
     * @param key  int
     * @return Node
     */
    private Node insertHelper(Node root, int key) {
        boolean redAfterRedConflict = false;   // will switch to true if a red node is followed by another red node

        if (root == null) {     // solves the recursion if the right position was found
            return (new Node(key));
        }

        // insert key at the correct position according to BST properties
        // this maybe generates a redAfterRedConflict / violates the BST properties
        // the fixup logic comes after this first insertion
        if (key < root.key) {    // insert into left subtree
            root.left = insertHelper(root.left, key);
            root.left.parent = root;
            if (root != this.root) {    // if root (of the current insertion) is not the BST root check for redAfterRedConflict
                if (root.isRed && root.left.isRed) {
                    redAfterRedConflict = true;
                }
            }
        } else {    // insert into right subtree
            root.right = insertHelper(root.right, key);
            root.right.parent = root;
            if (root != this.root) {    // if root (of the current insertion) is not the BST root check for redAfterRedConflict
                if (root.isRed && root.right.isRed) {
                    redAfterRedConflict = true;
                }
            }
        }

        // check if a rotation is needed
        root = checkRequiredRotations(root);

        // takes care of the previously detected redAfterRedConflict conflict
        if (redAfterRedConflict) {
            root = processRedRedConflict(root);
        }
        return (root);
    }

    public void insert(int key) {
        if (this.root == null) {    // BST insert is called it first time
            this.root = new Node(key);
            this.root.isRed = false;
        } else {
            this.root = insertHelper(this.root, key);
        }
    }


    static class DotOutput {
        int outputNCounter = 1;
        String complete;

        DotOutput(Node root) {
            String header = outputHeaderHelper();
            String nodeSection = outputNodeSectionHelper(root);
            String nLine = outputNLineHelper(outputNCounter);
            String redNodesLine = outputRedNodesLineHelper(root);

            complete = header + "\n" + redNodesLine + "\n" + nLine + "\n" + nodeSection + "}\n";
        }

        String get() {
            return complete;
        }

        void save(String fileName) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write(complete);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String outputHeaderHelper() {
            return "digraph G {\n" +
                    "\tgraph [ratio=.48; ordering=\"out\"];\n" +
                    "\tnode [style=filled, color=black, shape=circle, width=.6 \n" +
                    "\t\tfontname=Helvetica, fontweight=bold, fontcolor=white, \n" +
                    "\t\tfontsize=24, fixedsize=true];\n" +
                    "\t{rank = same;};\n";
        }

        private String outputNodeSectionHelper(Node root) {
            String data = "";
            if (root != null) {
                if (root.left == null) {
                    data += "\t" + root.key + " -> n" + outputNCounter++ + ";\n";
                } else {
                    data += "\t" + root.key + " -> " + root.left.key + ";\n";
                    data += outputNodeSectionHelper(root.left);
                }

                if (root.right == null) {
                    data += "\t" + root.key + " -> n" + outputNCounter++ + ";\n";
                } else {
                    data += "\t" + root.key + " -> " + root.right.key + ";\n";
                    data += outputNodeSectionHelper(root.right);
                }
            }
            return data;
        }

        private void redNodesCollector(Node root, ArrayList<Integer> redNodes) {
            if (root != null) {
                if (root.isRed) {
                    redNodes.add(root.key);
                }

                redNodesCollector(root.left, redNodes);
                redNodesCollector(root.right, redNodes);
            }
        }

        private String outputRedNodesLineHelper(Node root) {
            StringBuilder line = new StringBuilder();
            ArrayList<Integer> redNodes = new ArrayList<>();
            redNodesCollector(root, redNodes);

            Iterator<Integer> it = redNodes.iterator();
            while (it.hasNext()) {
                line.append(it.next());
                if (it.hasNext()) {
                    line.append(", ");
                }
            }

            if (redNodes.size() > 0) {
                return "\t" + line + "\n\t[fillcolor=red];\n";
            }

            return line.toString();
        }

        private String outputNLineHelper(int currentOutputNCounter) {
            StringBuilder line = new StringBuilder("\t");
            for (int i = 1; i < currentOutputNCounter; i++) {
                line.append("n").append(i);
                if (i + 1 < currentOutputNCounter) {
                    line.append(", ");
                }
            }
            return line + "\n\t[label=\"NIL\", shape=record, width=.4,height=.25, fontsize=16];\n";
        }
    }

    /**
     * save generated dot format String in a file.
     *
     * @param fileName String
     */
    public void output(String fileName) {
        DotOutput o = new DotOutput(this.root);
        // System.out.println(o.get());

        if (fileName != null && !fileName.equals("")) {
            o.save(fileName);
        }
    }

    private void printTreeHelper(Node root, int space) {
        int i;
        if (root != null) {
            space = space + 10;
            printTreeHelper(root.right, space);
            System.out.print("\n");
            for (i = 10; i < space; i++) {
                System.out.print(" ");
            }
            System.out.printf("%d", root.key);
            System.out.print("\n");
            printTreeHelper(root.left, space);
        }
    }

    /**
     * function to print the tree in the console
     */
    public void printTree() {
        printTreeHelper(this.root, 0);
    }
}
