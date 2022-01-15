import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random r = new Random();
        RBT t = new RBT();

        ArrayList<Integer> insertedValues = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            int n = getRandomIntInRange(r, 1, 100);

            if (insertedValues.contains(n)) {
                i--;
                continue;
            }

            t.insert(n);
            insertedValues.add(n);

            t.output("/tmp/dot/testat2/brt" + i + ".dot");
        }

        /*
        Execute to get a pdf with images from the java generated dot files:

        nix-shell -p graphviz imagemagick
        > for ((x=0; x<15; x++)); do dot -Tpng /tmp/dot/testat2/brt${x}.dot > /tmp/dot/testat2/brt${x}.png ; done
        > ls -ltr /tmp/dot/testat2/brt*.png | awk -F ' ' '{print $9}' | tr '\n' ' ' | sed 's/$/\ testat2_input_operations.pdf/' | xargs convert
        */
    }

    private static int getRandomIntInRange(Random r, int min, int max) {
        return r.nextInt((max - min) + 1) + min;
    }
}
