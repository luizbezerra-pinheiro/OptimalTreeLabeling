package optimalTreeLabeling;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

public class Node {
    int index;
    int label;
    LinkedList<Node> children;
    int sizedictionary; // it keeps the letters already used bellow this tree
    int[] weightMin;
    int weight;
    Node father;
    int height;


    public Node(int index, int label) {
        super();
        this.index = index;
        this.label = label;
        this.children = new LinkedList<>();
        this.sizedictionary = 0;
        this.weightMin = new int[52]; // 2*26
        this.father = null;
        this.height = 0;
        this.weight = 0;
    }

    public Node() {
        this.index = 0;
        this.label = 0;
        this.children = new LinkedList<>();
        this.sizedictionary = 0;
        this.weightMin = new int[52]; // 2*26
        this.father = null;
        this.height = 0;
        this.weight = 0;
    }


    public Node(BufferedReader bufferedReader, int N, int l) {

        int[] dictionary_used = new int[26];
        int sizedictionary = 0;
        // This will reference one line at a time
        String line = null;
        // Always wrap FileReader in BufferedReader.
        try {
            ArrayList<String> lines = new ArrayList<String>();
            Vector<Node> leafs = new Vector<Node>();
            Vector<Node> nodes = new Vector<Node>();
            String[] strArr;
            for (int i = 0; i < N; i++) {
                nodes.add(new Node(i, 0));
            }

            //Read and save the first n lines
            for (int k = 0; k < N - 1; k++) {
                line = bufferedReader.readLine();
                lines.add(line);
            }

            //Read the information about the leafs
            for (int k = 0; k < l; k++) {
                line = bufferedReader.readLine();
                strArr = line.split("\\s+");
                int i = Integer.parseInt(strArr[0]) - 1;
                LinkedList<Character> labelchar = new LinkedList<>();
                if (strArr[1].charAt(0) != '$') {
                    for (int j = 0; j < strArr[1].length(); j++) {
                        labelchar.add(strArr[1].charAt(j));
                        if(dictionary_used[strArr[1].charAt(j) - 'A'] == 0) {
                            dictionary_used[strArr[1].charAt(j) - 'A'] = 1;
                            sizedictionary+=1;
                        }
                    }
                }
                int label = getLabelValue(labelchar);
                nodes.get(i).label = label;
                leafs.add(nodes.get(i));
            }
            bufferedReader.close();


            //Read the information about the nodes and construct the tree
            int root = 0;
            line = lines.get(0);
            strArr = line.split("\\s+");
            int i = Integer.parseInt(strArr[0]) - 1;
            int j = Integer.parseInt(strArr[1]) - 1;
            if (!leafs.contains(nodes.get(i))) {    //nodes.get(i) is not a leaf so it is a root
                root =i;
                nodes.get(i).children.add(nodes.get(j));
                nodes.get(j).father = nodes.get(i);
            }
            else {
                root=j;
                nodes.get(j).children.add(nodes.get(i));
                nodes.get(i).father = nodes.get(j);
            }


            for (int k = 1; k < N - 1; k++) {
                line = lines.get(k);
                strArr = line.split("\\s+");
                i = Integer.parseInt(strArr[0]) - 1;
                j = Integer.parseInt(strArr[1]) - 1;

                if (leafs.contains(nodes.get(i)) || nodes.get(j).father != null) {    ///nodes.get(i) is a leaf
                    nodes.get(j).children.add(nodes.get(i));
                    nodes.get(i).father = nodes.get(j);
                } else if (leafs.contains(nodes.get(j)) || nodes.get(i).father != null) {  ///nodes.get(j) is a leaf
                    nodes.get(i).children.add(nodes.get(j));
                    nodes.get(j).father = nodes.get(i);
                } else {
                    if (i==root) {          //  nodes.get(i) is a root
                        nodes.get(i).children.add(nodes.get(j));
                        nodes.get(j).father = nodes.get(i);
                    }
                    else {                   //  nodes.get(j) is a root or it does not matter who is the root
                        nodes.get(j).children.add(nodes.get(i));
                        nodes.get(i).father = nodes.get(j);
                    }
                }
            }

            nodes.get(root).updateHeight();
            this.children = nodes.get(root).children;
            this.index = nodes.get(root).index;
            this.label = 0;
            this.height = nodes.get(root).height;
            this.weight = 0;
            this.sizedictionary = sizedictionary;


        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }

    }



    public static int getLabelValue (LinkedList<Character> label) {
        int count = 0;
        for (Character c : label) {
            if (c != '$') {
                int a = c - 'A';
                count += 1 << a;
            }

        }
        return count;
    }

    public void updateHeight() {
        int maxh = 0;
        if (!this.children.isEmpty()) {
            for (Node l : this.children) {
                l.updateHeight();
                if (l.height > maxh) {
                    maxh = l.height;
                }
            }
            this.height = 1 + maxh;
        } else {
            this.height = 0;
        }

    }

    public void updateTreeWeight() {
        for(Node t : this.children) {
            t.updateTreeWeight();
            this.weight += OptimalLabeling.distance(this.label, t.label) + t.weight;
        }

    }
}
