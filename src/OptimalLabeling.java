package optimalTreeLabeling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

public class OptimalLabeling {


	//	The idea used to solve the problem is to solve it by layers and by each character:
	//		First we try to insert 'A':
	//		To decide where we must insert to get the minimum layer we have the weighMin vector in each node
	//		weightMin has 52 spaces, for each letter in the alphabet we have 2 spaces,
	//		its function is to keep the minimum weight of its subtree whether  we put 'A' in this node or not
	//	To solve the problem we start from the layer just above the last layer (because the last layer is made of leafs)
	//	for each node in the layer we compute the the weight of its subtree if we put the letter or not
	//		Pra explicar o calculo precisa fazer o desenho com cada um dos casos
	//	When we finish the layer, we go to the layer just above.
	//	When we finish all the layers for the letter 'A' we will have a vector weighMin for each node that contains
	//the optimal decision for this letter, so we call insertOptimalLabel(l, root) that will go through the tree
	//starting from the root and will update the labels with the optimal decision

    public static int getOptimalLabel(Node root) {
        Vector<LinkedList<Node>> layers = new Vector<>(root.height);
        int LU = root.sizedictionary;
        layers = getLayers(root);

        for (int l = 0; l < LU; l++) {
            for (int i = root.height - 1; i >= 0; i--) { //the last layer's only leafs
                getOptimalLabel(l, layers.get(i), LU);
            }
            insertOptimalLabel(l, root);
        }
        root.updateTreeWeight();
        return root.weight;
    }

    private static void getOptimalLabel(int l, LinkedList<Node> layer, int LU) {
        // l is the letter we are testing to put on each node to get the weightMin
        for (Node t : layer) {

            if (!t.children.isEmpty()) {
                if (t.weightMin == null) {
                    t.weightMin = new int[2*LU];
                }
                int with_l = 0;
                int without_l = 0;
                for (Node child : t.children) {
                    if (child.children.isEmpty()) {
                        with_l += distance(t.label + (1 << l), child.label);
                        without_l += distance(t.label, child.label);
                    } else {
                        if (child.weightMin[2 * l]
                                + distance(t.label, child.label) < child.weightMin[2 * l + 1]
                                + distance(t.label, child.label + (1 << l))) {
                            without_l += child.weightMin[2 * l] + distance(t.label, child.label);
                        } else {
                            without_l += child.weightMin[2 * l + 1] + distance(t.label, child.label + (1 << l));
                        }
                        if (child.weightMin[2 * l]
                                + distance(t.label + (1 << l), child.label) < child.weightMin[2 * l + 1]
                                + distance(t.label + (1 << l), child.label + (1 << l))) {
                            with_l += child.weightMin[2 * l] + distance(t.label + (1 << l), child.label);
                        } else {
                            with_l += child.weightMin[2 * l + 1]
                                    + distance(t.label + (1 << l), child.label + (1 << l));
                        }
                    }
                }
                t.weightMin[2 * l] = without_l;
                t.weightMin[2 * l + 1] = with_l;
            }
        }

    }

    private static void insertOptimalLabel(int l, Node root) {
        if (root.weightMin[2 * l + 1] < root.weightMin[2 * l])
            root.label += 1 << l;
        for (Node child : root.children) {
            if (!child.children.isEmpty()) {
                if (child.weightMin[2 * l] + distance(root.label, child.label) < child.weightMin[2 * l + 1]
                        + distance(root.label, child.label + (1 << l))) {
                    // the decision of not putting l is optimal for child
                    child.weightMin[2 * l] = 0;
                } else {
                    // the decision of putting l is optimal for child
                    child.weightMin[2 * l + 1] = 0;
                }
                insertOptimalLabel(l, child);
            }
        }
    }


    private static Vector<LinkedList<Node>> getLayers(Node root) {
        Vector<LinkedList<Node>> layers = new Vector<>();
        LinkedList<Node> zerolevel = new LinkedList<>();
        zerolevel.add(root);
        layers.add(zerolevel);
        layers.add(root.children);
        for (int i = 1; i < root.height; i++) {
            layers.add(getNextLayer(layers.get(i)));
        }
        return layers;
    }

    private static LinkedList<Node> getNextLayer(LinkedList<Node> lastlevel) {
        LinkedList<Node> nextlevel = new LinkedList<>();
        for (Node n : lastlevel) {
            nextlevel.addAll(n.children);
        }
        return nextlevel;
    }


    static int distance(int label1, int label2) {
        int ret = 0;
        for (int i = 0; i < 26; i++) {
            if (((label1 ^ label2) & 1 << i) > 0) {
                ret++;
            }
        }
        return ret;
    }


    public static void main(String[] args) {

        for (int i = 0; i <= 10; i++) {
            System.out.println("Test number " + i);
            //long startTime = System.currentTimeMillis();
            long startTime = System.nanoTime();
            String fileName = "tests/labeling." + i + ".in";
            String line = null;
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            int N = 0, l = 0;
            try {
                fileReader = new FileReader(fileName);
                bufferedReader = new BufferedReader(fileReader);
                line = bufferedReader.readLine();
                String[] strArr = line.split("\\s+");
                N = Integer.parseInt(strArr[0]);
                l = Integer.parseInt(strArr[1]);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            Node root = new Node(bufferedReader, N, l);
            //System.out.println("Altura Otima: " + root.height);
            int totalweight = getOptimalLabel(root);

            //long stopTime = System.currentTimeMillis();
            long stopTime = System.nanoTime();
            long elapsedTime = stopTime - startTime;
            System.out.println("Total Weight: " + totalweight);
            System.out.println("Execution time: " + elapsedTime + "ns");
        }


    }
}
