import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * author: Helen Cui
 * date: 4/29/24
 *
 * purpose: implementing Huffman compression
 */

public class Compressor implements Huffman{
    //definitely missing some instance vars

    /**
     * Read file provided in pathName and count how many times each character appears
     * @param pathName - path to a file to read
     * @return - Map with a character as a key and the number of times the character appears in the file as value
     * @throws IOException
     */
    @Override
    public Map<Character, Long> countFrequencies(String pathName) throws IOException {

        Map<Character, Long> frequencymap = new HashMap<>();
        BufferedReader input = new BufferedReader(new FileReader(pathName)); //buffered reader = string pathname

        try{
            int tempInt = input.read();


        while (tempInt != -1){
            char tempChar = (char)tempInt;

        // if key doesnt exist, make it exist
            if(!frequencymap.containsKey(tempChar)){
                frequencymap.put(tempChar, (long) 1);}

            //increment
            else {
                frequencymap.put(tempChar, (frequencymap.get(tempChar) + 1));}

            tempInt = input.read();// move to next

        }}
        catch(IOException e){
            System.err.println("cannot read file" + e.getMessage());
        }
        finally {
            input.close();
        }

        return frequencymap;
    }

    /**
     * Construct a code tree from a map of frequency counts. Note: this code should handle the special
     * cases of empty files or files with a single character.
     *
     * @param frequencies a map of Characters with their frequency counts from countFrequencies
     * @return the code tree.
     */
    @Override
    public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies) {
        //make the entire tree --> purpose of this method
        TreeComparator comparator = new TreeComparator();
        PriorityQueue<BinaryTree<CodeTreeElement>> frequencyQueue = new PriorityQueue<>(comparator);
        Set<Character> characters = frequencies.keySet();


        //making a priority queue
        for (Character c : characters){
            CodeTreeElement tempE  = new CodeTreeElement(frequencies.get(c),c);
            BinaryTree<CodeTreeElement> tempTree= new BinaryTree<>(tempE);
            frequencyQueue.add(tempTree);
        }

        while (frequencyQueue.size() > 1) {
            BinaryTree<CodeTreeElement> t1 = frequencyQueue.poll();
            BinaryTree<CodeTreeElement> t2 = frequencyQueue.poll();
            CodeTreeElement r = new CodeTreeElement((t1.data.myFrequency + t2.data.myFrequency), null);
            BinaryTree<CodeTreeElement> babyBinary = new BinaryTree<>(r, t1, t2);
            frequencyQueue.add(babyBinary);
        }

        return frequencyQueue.poll();
        }


    /**
     * Computes the code for all characters in the tree and enters them
     * into a map where the key is a character and the value is the code of 1's and 0's representing
     * that character.
     *
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @return the map from characters to codes
     */
    @Override
    public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree) {
        //map that pairs characters with their code works: pair each character with a string of 0 and 1
        // do this through a single traversal through the tree

        Map<Character, String> charCodeMap = new TreeMap<>();
        String pathsofar = "";
        if (codeTree.size() == 0){
            return charCodeMap;
        }

        if (codeTree.size() == 1){
            charCodeMap.put(codeTree.getData().getChar(), "0");
            return charCodeMap;
        }

        computeCodesHelper(charCodeMap, codeTree, pathsofar);

        return charCodeMap;

    }

    /**
     * Accumulator helper function for computeCodes which recursively traverses tree
     * to create the code for each char in the frequence tree
     * @param codeTree the tree for encoding characters produced by makeCodeTree
     * @param charCodeMap the map that we are storing the computed codes into
     * @param pathsofar the string used that is being added to
     * @return the map from characters to codes
     */
//HELPERS
    public void computeCodesHelper(Map<Character, String> charCodeMap, BinaryTree<CodeTreeElement> codeTree, String pathsofar) {
        // recursive helper function

        if(codeTree.isLeaf()){
            charCodeMap.put(codeTree.getData().getChar(), pathsofar);
        }

        if(codeTree.hasLeft()){
             //left edge

            computeCodesHelper(charCodeMap, codeTree.getLeft(), pathsofar + 0 );

        }
        if(codeTree.hasRight()){
            //right edge

            computeCodesHelper(charCodeMap, codeTree.getRight(), pathsofar + 1);
        }

    }

    /**
     * Compress the file pathName and store compressed representation in compressedPathName.
     * @param codeMap - Map of characters to codes produced by computeCodes
     * @param pathName - File to compress
     * @param compressedPathName - Store the compressed data in this file
     * @throws IOException
     *  */
    @Override
    public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException {
    // read the next character in text file
        BufferedReader input = new BufferedReader(new FileReader(pathName));
        BufferedBitWriter output = new BufferedBitWriter(compressedPathName);

        try {
            int temp = input.read(); //assign first val

            while (temp != -1){
                char c = (char) temp; //cast to char
                String bit = codeMap.get(c);
                //write sequence of 0s and 1s as an output file
                //string is null,,, so like
                for (char character : bit.toCharArray()){
                    if (character == '0') output.writeBit(false);
                    else {output.writeBit(true);}
                }
                temp = input.read(); //move to next char
            }

        } catch(IOException e){
            System.err.println("Unable to read file. Error: " + e.getMessage());
        }
        finally {
            input.close();
            output.close();
        }

    }

    /**
     * Decompress file compressedPathName and store plain text in decompressedPathName.
     * @param compressedPathName - file created by compressFile
     * @param decompressedPathName - store the decompressed text in this file, contents should match the original file before compressFile
     * @param codeTree - Tree mapping compressed data to characters
     * @throws IOException
     */
    @Override
    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException {
// read one file then compress to another
        BinaryTree<CodeTreeElement> curr = codeTree;
        BufferedBitReader bitInput = null;
        BufferedWriter output = null;


        try{
            bitInput = new BufferedBitReader(compressedPathName);
            output = new BufferedWriter(new FileWriter(decompressedPathName));
            if (curr == null){return;}


            while (bitInput.hasNext()) { //ITS NOT READING AT ALL
                boolean bit = bitInput.readBit(); //boolean values are false and true: correspond w 0 and 1;

                //traverses down tree correct path for char LIKE DIRECTIONS
                if (bit) {
                    if (curr.hasRight()) {
                        curr = curr.getRight();
                    } }
                else if (curr.hasLeft()) {
                    curr = curr.getLeft();
                    }

                    //destination reached!
                if (curr.isLeaf()) {
                    output.write(curr.getData().getChar());
                    curr = codeTree; //reset
                    }
                }

            }catch (IOException e){
            System.err.println("does not exist bro");

        }
        bitInput.close();
        output.close();
        }


    public static void main(String[] args) throws IOException {
        Compressor compresstest = new Compressor();

        //test frequency map and tree
        Map<Character, Long> frequencyMap = compresstest.countFrequencies("inputs/test");
        System.out.println(frequencyMap.keySet());
        BinaryTree<CodeTreeElement> frequencyTree = compresstest.makeCodeTree(frequencyMap);


        //test if compute codes registers --> all of it registers but its not quite right; the tree is correct tho
        //this doesnt read my file for some reason
        Map<Character, String> computeCodes = compresstest.computeCodes(frequencyTree);
        System.out.println(computeCodes);


        //test if compress works
        compresstest.compressFile(computeCodes, "inputs/test", "inputs/test_compressed");

        //test if decompress works --> MY DECOMPRESSOR DOES NOT WORK
        compresstest.decompressFile("inputs/test_compressed", "inputs/test_decompressed", frequencyTree);

        //USCONST
        Compressor usc = new Compressor();
        Map<Character, Long> freq = usc.countFrequencies("inputs/USConstitution.txt");
       /* System.out.println(freq.keySet());*/
        BinaryTree<CodeTreeElement> tree = usc.makeCodeTree(freq);
        Map<Character, String> comp = usc.computeCodes(tree);
        /*System.out.println(comp);*/
        usc.compressFile(comp, "inputs/USConstitution.txt", "inputs/USConstitution_compressed.txt");
        usc.decompressFile("inputs/USConstitution_compressed.txt","inputs/USConstitution_decompressed.txt", tree);

        //WAR AND PEACE
        Compressor wp = new Compressor();
        Map<Character, Long> freq1 = wp.countFrequencies("inputs/WarAndPeace.txt");
        /* System.out.println(freq.keySet());*/
        BinaryTree<CodeTreeElement> tree1 = wp.makeCodeTree(freq1);
        Map<Character, String> comp1 = wp.computeCodes(tree1);
        /*System.out.println(comp);*/
        wp.compressFile(comp1, "inputs/WarAndPeace.txt", "inputs/WarAndPeace_compressed.txt");
        wp.decompressFile("inputs/WarAndPeace_compressed.txt","inputs/WarAndPeace_decompressed.txt", tree1);



    }
}
