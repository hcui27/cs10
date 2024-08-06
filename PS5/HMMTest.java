import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class HMMTest {
    public static void main(String[] args) throws IOException {

        //checking to see if handleSentence works
        HMM test = new HMM();
        ArrayList<String[]> testwords = test.handleString("ps5/simple-train-sentences.txt");
        ArrayList<String[]> testtags = test.handleString("ps5/simple-train-tags.txt");
        ArrayList<String[]> brownwords = test.handleString("ps5/brown-train-sentences.txt");
        ArrayList<String[]> browntags = test.handleString("ps5/brown-train-tags.txt");


        ArrayList<String[]> testSentence = test.handleString("hello my name is");

for (int i = 0; i < testwords.size(); i++ ){
    test.training(testwords.get(i), testtags.get(i));
}

for (int i = 0; i < brownwords.size(); i++ ){
      test.training(brownwords.get(i), browntags.get(i));
        }
//for (String key : test.transitions.keySet()){
//  System.out.println(key);
//   System.out.println(test.transitions.get(key).keySet());
//  System.out.println("next + frequencies");
//  for (String key2 : test.transitions.get(key).keySet()){
//      System.out.println(key2 + " : " + test.transitions.get(key).get(key2));
// }}

//for (String key : test.observations.keySet()){
//    System.out.println(key);
//    System.out.println(test.observations.get(key).keySet());
//    System.out.println("word + frequencies");
//    for (String key2 : test.observations.get(key).keySet()){
//        System.out.println(key2 + " : " + test.observations.get(key).get(key2));
//            }
//        }
//System.out.println("testing my calculate prob");
test.calculateProb();
//Set<String> testprob = test.obsProb.get("p").keySet();

//for (String s : testprob){
//    System.out.println(s + " ");
//    System.out.println(test.obsProb.get("p").get(s));
//}

//System.out.println("testing my viterbi");
//        test.handleString("ps5/simple-test-sentences.txt");
//for (String[] tester : test.handleString("ps5/simple-test-sentences.txt")){
//    ArrayList<String> tagged = test.tagFinder(tester);
//    System.out.print("\n");
//    for (String word: tagged){
//        System.out.print(word + " ");}
//}
//test.fileInput("ps5/simple-test-sentences.txt", "ps5/simple-test-tags.txt");
//test.fileInput("ps5/brown-test-sentences.txt", "ps5/brown-test-tags.txt");

test.consoleInput();


    }

    }



