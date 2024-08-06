
import java.io.*;
import java.util.*;

public class HMM {
        public Map < String, Map < String, Integer >> transitions; //inner map is for transition frequency
        public Map < String, Map < String, Integer >> observations; //inner map is for word count
        public Map < String, Map < String, Double >> transProb; //keys for transProb String
        public Map < String, Map < String, Double >> obsProb;

        // state -> (character -> [next states])
        // note the difference from DFA: can have multiple different transitions from state for character

//originally before it has been trained give it an original value of like 1 or smth
public HMM(){
        transitions = new HashMap<>();
        observations = new HashMap<>();
}
        /**
         * this function should just read each line of the training file or input etc.
         * turning it into a string[] for training or for tagging
         *
         * hypothetically: would want to run this twice to get a thing of tags
         * and a thing of words
         * @param filepath
         * @return ArrayList<String[]>
         */

        //somewhere in here i have to add # as a start
        public static ArrayList<String[]> handleString(String filepath) throws IOException {

                ArrayList<String[]> sentences = new ArrayList<>();
                try{
                        BufferedReader input = new BufferedReader(new FileReader(filepath));
                        String sentence;
                        String start = "# ";
                        String tempsentence;


                        while ((tempsentence = input.readLine()) != null){
                                sentence = start + tempsentence;
                                String[] temp = sentence.split("\\s");
                                for (int i = 0; i< temp.length; i++){
                                        temp[i] = temp[i].toLowerCase();
                                }
                                sentences.add(temp);
                        }

                        input.close();
                }catch (IOException e){ //if file doesnt exist we're going to assume that this is command
                        //line input
                        String[] sentence = filepath.split("\\s");
                        sentences.add(sentence);
                }

        return sentences;
}
        /**
         * this function fills the transitions and observations
         * only made to handle one line of each file at a time;
         * will have to use a for loop in order to do this
         * @param input : the words in questions
         * @param tags : the actual word tags
         */
        public void training(String[] input, String[] tags) {

                //fill observations table
                for (int i = 0; i < input.length; i++) {
                        // state row exists
                        if (observations.containsKey(tags[i])) {
                                // word exists in map already
                                if (observations.get(tags[i]).containsKey(input[i])) {
                                        int initial = observations.get(tags[i]).get(input[i]);
                                        //increment frequency by 1 if exists in observatios map
                                        observations.get(tags[i]).put(input[i], initial + 1);
                                } else { //tag exists but not the word in the word freq map
                                        observations.get(tags[i]).put(input[i], 1);
                                }
                        }
                        //observations doesn't contain the tag already, need to create new row and word
                        else {//initialize map
                                Map<String, Integer> wordfreq = new HashMap<>();
                                wordfreq.put(input[i], 1);
                                observations.put(tags[i], wordfreq);
                        }
                }


                //fill transitions table
                int counter = 0;
                String curr;
                String next;

                while (counter <= (tags.length - 2)){
                        curr = tags[counter];
                        next = tags[counter + 1];
                        //transitions doesn't have a row for current state
                   if (transitions.containsKey(curr)){
                            //has row (state) but not next column
                           if(transitions.get(curr).containsKey(next)){
                                    int initial = transitions.get(curr).get(next);
                                    transitions.get(curr).put(next,initial+1);
                            }
                            else{ //row and column exists, but not this word specifically
                                   transitions.get(curr).put(next,1);}

                    }
                    //transitions doesn't contain the tag already, need to create new row and word
                    else{ //initialize map
                           Map <String, Integer> transfreq = new HashMap<>();
                          transfreq.put(next, 1);
                           transitions.put(curr, transfreq);

                }
                    counter = counter + 1;
                }}




        public void calculateProb() {
                transProb = new HashMap<>();
                obsProb = new HashMap<>();

                Set<String> transitionkeys = transitions.keySet();
                Set<String> observationkeys = observations.keySet();

                for (String key : observationkeys){
                        Set<String> tempWords = observations.get(key).keySet();
                        int total = 0;
                        //generate the total for each row
                        for (String word :tempWords){
                                total = total + observations.get(key).get(word);
                        }
                        for (String word :tempWords) {
                                //for each word in an observation row; divide freq by total and find log prob
                                double tempProb = Math.log(observations.get(key).get(word)/((double)total));

                                //observation probability contains the key, thus map has been initialized already
                                if (obsProb.containsKey(key)){
                                        obsProb.get(key).put(word, tempProb);

                                }
                                else{// we can assume that the map needs to be initialized, bc there should be no
                                        //repeats in our table
                                        Map<String, Double> probMap = new HashMap<>();
                                        probMap.put(word, tempProb);
                                        obsProb.put(key, probMap);

                                }

                        }
                }

                for (String key : transitionkeys){
                        Set<String> tempNext = transitions.get(key).keySet();
                        int total = 0;
                        //generate the total for each row

                        for (String next :tempNext){
                                total = total + transitions.get(key).get(next);
                        }

                        for (String next :tempNext) {
                                //for each word in an observation row; divide freq by total and find log prob
                                double tempProb = Math.log(transitions.get(key).get(next)/((double)total));

                                //so the keys to the transitions have a

                                //observation probability contains the key, thus map has been initialized already
                                if (transProb.containsKey(key)){
                                        transProb.get(key).put(next, tempProb);
                                }
                                else{// we can assume that the map needs to be initialized, bc there should be no
                                        //repeats in our table
                                        Map<String, Double> probMap = new HashMap<>();
                                        probMap.put(next, tempProb);
                                        transProb.put(key, probMap);}
        }}}

        /**
         * generate the list of tags for any string array
         * @param tobetagged
         * @return
         */
        public ArrayList<String> tagFinder(String[] tobetagged){
                //use viterbi to create a list of
                int hiddenObs = -100;
                ArrayList<Map<String, String>> goingback = new ArrayList<>();
                Set<String> currStates = new HashSet<>();
                Map<String, Double> currScores = new HashMap<>();

                // currStates = { start }
                // * currScores = map { start=0 }
                currStates.add("#");
                currScores.put("#", 0.0);

                for (int i = 0; i < (tobetagged.length-1); i++){
                        Set<String> nextStates = new HashSet<>();
                        Map<String, Double> nextScores = new HashMap<>();
                        Map < String, String > backtrace = new HashMap <>();


                        for (String curr : currScores.keySet()){
                                //transitions won't have when curr = .
                                if (!transitions.containsKey(curr)){ //not actually a good way to handle this issue
                                        continue;
                                }
                                for (String next : transitions.get(curr).keySet()){
                                        nextStates.add(next);
                                        double nextScore;

                                        if (!obsProb.get(next).containsKey(tobetagged[i+1])){ // if the thing exists there
                                                nextScore = currScores.get(curr) //path to here
                                                        + transProb.get(curr).get(next) + hiddenObs; // take a step to there
                                        }

                                        else {
                                                nextScore = currScores.get(curr) //path to here
                                                + transProb.get(curr).get(next) // transition score
                                                + obsProb.get(next).get(tobetagged[i+1]);
                                        }

                                        if (!nextScores.containsKey(next) || nextScore > nextScores.get(next)){
                                                nextScores.put(next, nextScore);
                                                backtrace.put(next, curr);
                                        }}}
                        goingback.add(backtrace);
                        currStates = nextStates;
                        currScores = nextScores;
                }
                //get the largest score
                String largest = null;

                for (String key : currScores.keySet()){
                        if (largest == null){ //if it doesnt exist make largest = key
                                largest = key;
                        }
                        else if (!(currScores.get(key) > currScores.get(largest))){
                                largest = key; //else find max
                        }
                }

                //follow backpointers
                ArrayList<String> path = new ArrayList<>();
                String current = largest;
                for (int j = goingback.size()-1; j > -1; j--){
                        //add state w the largest score to return path
                        path.add(0, current);
                        current = goingback.get(j).get(current);
                }
                return path;
        }





    public void consoleInput() throws IOException {
           System.out.println("Enter what you would like to be tagged:");
           System.out.println(">:");
           Scanner input = new Scanner(System.in);
           String words = "# " + input.nextLine();
                //there is the assumption that somebody isn't entering things in multiple lines

           ArrayList<String[]> tobetagged = handleString(words); // should only be one String[]
           ArrayList<String> tags = tagFinder(tobetagged.get(0));
           String s = "";

           for (int i = 0; i < tags.size(); i++){
                        s = s + tobetagged.get(0)[i+1] + " ("+ tags.get(i) + ") ";
                }

           System.out.println(s);

    }

    public void fileInput(String testfile, String checktags) throws IOException {
        int correct = 0;
        int wrong = 0;
        ArrayList<String[]> raw = handleString(testfile);
        ArrayList<String[]> check = handleString(checktags); //check answers

        //to compare each String[] individually
        int size = raw.size();

        for (int i = 0; i < size; i++){
                ArrayList<String> temptags = tagFinder(raw.get(i));

                //keep track of num of tags per line
                int tagsize = temptags.size();
                for (int j = 0; j < tagsize; j++){
                        //there is a # on the normal handlestring
                        if (Objects.equals(temptags.get(j), check.get(i)[j + 1])){
                                correct = correct + 1;
                        }
                        else {
                                wrong = wrong + 1;
                }
        }
    }
            System.out.println( "correct tags: "+ correct + " wrong tags: "+ wrong);
}}
