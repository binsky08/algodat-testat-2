# algodat-testat-2

## Task
Implementation of a Red Black Tree. Executes 15 insert operations with random numbers between 1 and 100 starting with an empty tree.

## Usage
install [graphviz](https://graphviz.org/download/)

<ol>
<li>open Main.java</li>
<li>adjust target path (where .dot files shall be saved)</li>
<li>execute javac Main</li>
<li>execute java Main</li>
</ol>

### Under Linux
Execute to get a pdf with images from the java generated dot files:

```bash
nix-shell -p graphviz imagemagick   # required for nixos
> for ((x=0; x<15; x++)); do dot -Tpng /tmp/dot/testat2/brt${x}.dot > /tmp/dot/testat2/brt${x}.png ; done
> ls -ltr /tmp/dot/testat2/brt*.png | awk -F ' ' '{print $9}' | tr '\n' ' ' | sed 's/$/\ testat2_input_operations.pdf/' | xargs convert
```

### Under Windows:
Change to the directory with the .dot files (target path).
To generate svg files out of dot-files execute in PowerShell:
```
for ($x=0; $x -le 14; $x++ ) {dot -Tsvg brt${x}.dot > brt${x}.svg}
```
