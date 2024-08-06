import java.io.*;
import java.util.*;

/**
 * Helen Cui : bacon game run file
 * @param <V>
 * @param <E>
 */

public class BaconGame<V,E> { //command line interface just means you can add input directly right?
    public Graph<String, Set<String>> universe; //vertices = names; sets of edges = movie names
    public Map<Integer,String> actorID;
    public Map<Integer,String> movieID;
    public Map<Integer,Set<String>> movieActors;
    public String currentCenter;

    //initializes instance vars
    public BaconGame(){
        actorID = new HashMap<>();
        movieID = new HashMap<>();
        movieActors = new HashMap<>();
        universe = new AdjacencyMapGraph<>();
        currentCenter = "Kevin Bacon";
    }

    /**
     * processes/loads all the maps from file
     * @param actorsfile
     * @param moviesfile
     * @param moviesactorsfile
     * @throws IOException
     */

    public void loadMaps(String actorsfile, String moviesfile, String moviesactorsfile) throws IOException {
        //load actorID maps


        BufferedReader aID = new BufferedReader(new FileReader(actorsfile));
        String tempLine;
        while ((tempLine = aID.readLine()) != null){
            String[] actors = tempLine.split("\\|");
            actorID.put(Integer.parseInt(actors[0]), actors[1]);

        }
        aID.close();

        //load movieID maps
        BufferedReader mID = new BufferedReader(new FileReader(moviesfile));
        String tempMovies;
        while ((tempMovies = mID.readLine()) != null){
            String[] movies = tempMovies.split("\\|");
            movieID.put(Integer.parseInt(movies[0]), movies[1]);
        }
        mID.close();

        //load movie ID to actor ID map
        BufferedReader moviesToActors = new BufferedReader(new FileReader(moviesactorsfile));
        String tempmtoActors;
        while ((tempmtoActors = moviesToActors.readLine()) != null){
            String[] mToA = tempmtoActors.split("\\|");
            int currMovieID = Integer.parseInt(mToA[0]);
            int currActorID = Integer.parseInt(mToA[1]);

            if (movieActors.containsKey(currMovieID)){
                movieActors.get(currMovieID).add(actorID.get(currActorID));
            }
            else {
                Set<String> costars = new HashSet<>();
                costars.add(actorID.get(currActorID));
                movieActors.put(currMovieID, costars);
            }}
        moviesToActors.close();
    }

    /**
     * loads the universe from the maps given
     */

    public void loadUniverse(){
    //load graph V(actors) edges (sets of movies)
    Set<Integer> actorIDSet = actorID.keySet();
    Set<Integer> movieIDSet = movieID.keySet();
        for (int i : actorIDSet) {
            String currActor = actorID.get(i);
            universe.insertVertex(currActor);
        }
        for (int i : movieIDSet) {
            String currMovie = movieID.get(i);
            Set<String> currActors = movieActors.get(i);

            if (!(currActors == null)){
            for (String actor1Name : currActors) {
                for (String actor2Name : currActors) {
                    if (universe.hasEdge(actor1Name, actor2Name)) {
                        universe.getLabel(actor1Name, actor2Name).add(currMovie);
                    } else {
                        Set<String> edgeMovies = new HashSet<>();
                        edgeMovies.add(currMovie);
                        universe.insertUndirected(actor1Name, actor2Name, edgeMovies); //directed?
                    }}}}}}

    public void printCommands(){

        System.out.println("Commands\n" +
                "c : (positive number) list <#> top centers of the universe sorted by average separation\n"+
                "d : list actors sorted by degree, with degree between low and high\n"+
                "i : list actors with infinite separation from the current centern\n"+
                "p : find path from <name> to current center of the universen\n"+
                "s : list actors sorted by non-infinite separation from the current center, with separation between low and high\n"+
                "u : make <name> the center of the universe\n"+
                "q: quit game");}


    /**
     * begins scanner and also takes triggers for helper functions
     * @throws IOException
     */

    public void takeresponse() throws IOException {
        System.out.println( "> " + currentCenter + "'s game");
        Scanner in = new Scanner(System.in);
        String command;

        while (true) {
            System.out.print(">");
            command = in.nextLine();
            String key[] = command.split("\\s");
            String currinput = key[0];

            //Pressing c calls the list top method to return sorted items
            if(currinput.equals("c")) {
                System.out.print("Enter number of actors to meet>");
                currinput = in.nextLine();
                //catch if it isnt a number
                try{
                    Integer.parseInt(currinput);
                }catch(NumberFormatException e){
                    System.err.println("invalid input");
                    continue;
                }
                helperc(Integer.parseInt(currinput));
            }
            //Returns actors sorted by their number of neighbours; given high/low
            else if(currinput.equals("d")){
                System.out.print("Enter low");
                currinput = in.nextLine();

                try{
                    Integer.parseInt(currinput);
                }catch(NumberFormatException e){
                    System.err.println("invalid input") ;
                    continue;
                }
                int low = Integer.parseInt(currinput);


                System.out.print("Enter high");
                currinput = in.nextLine();


                try{
                    Integer.parseInt(currinput);
                }catch(NumberFormatException e){
                    System.err.println("invalid input");
                    continue;
                }
                int high = Integer.parseInt(currinput);
                helperd(low,high);

            }

            //Returns actors with infinite separation from the center actor
            else if(currinput.equals("i")) {
                helperi();
            }
            //Returns the path from enterd actor to the current center
            else if(currinput.equals("p")) {
                System.out.print("Enter actor name>");
                currinput = in.nextLine();
                if (universe.hasVertex(currinput)){
                    helperp(currinput);
                }
                else System.out.print("Invalid actor name");

            }
            //Sets the center to the input vertex
            else if(currinput.equals("u")) {
                System.out.print("Enter actor name>");
                currinput = in.nextLine();
                if (universe.hasVertex(currinput)){
                    helperu(currinput);
                }
                else System.out.print("Invalid actor name");

            }
            //Prints the actors sorted by their non - infinite distance from  the current center
            else if (currinput.equals("s")) {
                System.out.print("Enter low");
                currinput = in.nextLine();
                try{
                    Integer.parseInt(currinput);
                }catch(NumberFormatException e){
                    System.err.println("invalid input");
                    continue;
                }

                int low = Integer.parseInt(currinput);
                System.out.print("Enter high");
                currinput = in.nextLine();
                try{
                    Integer.parseInt(currinput);
                }catch(NumberFormatException e){
                    System.err.println("invalid input");
                    continue;
                }
                int high = Integer.parseInt(currinput);
                helpers(low,high);
            }

            //Quits the aplication from running
            else if (currinput.equals("q")) {
                quit();
            }

            else System.out.println("Invalid Input");
        }}


    //  HELPER METHODS

    /**
     * //c <#>: list top (positive number) or bottom (negative) <#> centers of the universe,
     * sorted by average separation
     * @param num
     */
    public void helperc(int num){
        ArrayList<String> listofvertices = new ArrayList<>();
        for(String current:universe.vertices()) {
            listofvertices.add(current);
        }
        //Sorts vertices with respect to average separation
        System.out.println("Sorted actors by average separation");

        listofvertices.sort( (n1,n2)-> (int) BFSGraphLib.averageSeparation(BFSGraphLib.bfs(universe,n2),n2)-(int)BFSGraphLib.averageSeparation(BFSGraphLib.bfs(universe,n1),n1));
        for (int i = 0; i< num; i++) System.out.println(listofvertices.get(i));
    }




        //compareTo changes depending on if num is positive or negative, run average length on each center



    /** //d <low> <high>: list actors sorted by degree, with degree between low and high
     * EXCLUSIVE
     * @param low
     * @param high
     */

    public void helperd(int low, int high){
        PriorityQueue<String> minQueue = new PriorityQueue<String>((actor1, actor2) ->
                (int) (universe.inDegree(actor1) - universe.inDegree(actor2)));

        for (String actor : universe.vertices()){
                minQueue.add(actor);

        while (!minQueue.isEmpty()) {
            String tempactorName = minQueue.poll();
            int temp = (universe.inDegree(tempactorName));

            if (temp >= low && temp <= high) System.out.println(tempactorName);
        }
    }}

    /**
     * i: list actors with infinite separation from the current center
     */

    public void helperi(){
        Graph<String, Set<String>> bfs = BFSGraphLib.bfs(universe, currentCenter);
        Set<String> missingV = BFSGraphLib.missingVertices(universe,bfs);

        String s = currentCenter + " has not costarred with";
        for (String name : missingV) s = s + "\n" + name;
        System.out.println(s);

    }

    /**
     * //p <name>: find path from <name> to current center of the universe
     * @param name
     */

    public void helperp(String name){
        Graph<String, Set<String>> bfs = BFSGraphLib.bfs(universe, currentCenter);
        List<String> bestPath = BFSGraphLib.getPath(bfs, name);
        if (bestPath.isEmpty()) {
            System.out.println(name + " is not in the actor network of " + currentCenter);
            return;}

        System.out.println(name);
        System.out.println(name + "'s number is "+ (bestPath.size()-1));

        //go into universe; use list<string> as a key
        //print name print edge print next vertex
        //vertex1 == vertex 2; vertex 2 = vertex.next

        for (int i = 0; i < (bestPath.size()-1); i++){
            String vertex1 = bestPath.get(i);
            String vertex2 = bestPath.get(i+1);
            String s = vertex1 + " appeared in ";

            Set<String> movies = universe.getLabel(vertex1, vertex2);
            for (String movie: movies){
                s = s + movie + " ";
            }
            s = s + "with " + vertex2;

            System.out.println(s);
        }
        

    }

    public void helpers(int low, int high){
//s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n"+
        Graph<String, Set<String>> bfs = BFSGraphLib.bfs(universe, currentCenter);
        PriorityQueue<String> minQueue = new PriorityQueue<String>((actor1, actor2) ->
                (int) (BFSGraphLib.getPath(bfs,actor1).size() - BFSGraphLib.getPath(bfs,actor2).size()));

        for (String vertex : bfs.vertices()){
            minQueue.add(vertex);
        }

        System.out.println(currentCenter + "'s costars:");

        while (!minQueue.isEmpty()){
            String tempactorName = minQueue.poll();
            int pathsize = BFSGraphLib.getPath(bfs, tempactorName).size()-1;
            if (pathsize > low && pathsize < high){
                System.out.println(tempactorName + " is " + pathsize + " movies away from " + currentCenter);
            }
        }

    }

    public void helperu(String name){
        //u <name>: make <name> the center of the universe
        currentCenter = name;
        Graph<String, Set<String>> bfs = BFSGraphLib.bfs(universe, currentCenter);
        Set<String> missingV = BFSGraphLib.missingVertices(universe,bfs);

        // all of the things into the string
        int numofCostars = universe.numVertices() - missingV.size();
        double averagePathsize = BFSGraphLib.averageSeparation(BFSGraphLib.bfs(universe,name), name);

        String s = currentCenter + " is now the center of the acting universe, connected to " + numofCostars +
                "/" + universe.numVertices() + " with average separation " + averagePathsize;

        System.out.println(s);

    }

    public static void quit() {
        Runtime.getRuntime().exit(1);

    }

    public void startgame(String actorsfile, String moviesfile, String moviesactorsfile) throws IOException {
        loadMaps(actorsfile, moviesfile, moviesactorsfile);
        loadUniverse();

        printCommands();
        takeresponse();

    }



    }

