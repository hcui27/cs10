import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<CodeTreeElement>> {
    @Override
    public int compare(BinaryTree<CodeTreeElement> t1, BinaryTree<CodeTreeElement> t2) {
        if (t1.data.getFrequency() < t2.data.getFrequency()){ //less than
            return -1;
        }
        if (t1.data.getFrequency() > t2.data.getFrequency()){ //greater than
            return 1;
        }
        return 0; //equals
}}
