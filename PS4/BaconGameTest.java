import java.io.IOException;
import java.util.List;
import java.util.Set;

public class BaconGameTest {
    public static void main(String[] args) throws IOException {

        //load maps test: load maps works;
       BaconGame<String, Set<String>> test1 = new BaconGame();
        test1.loadMaps("ps4/actorsTest.txt", "ps4/moviesTest.txt", "ps4/movie-actorsTest.txt");
//        System.out.println(test.actorID);
//        System.out.println(test.movieID);
//        System.out.println(test.movieActors);

        //load universe -- completed
        //test1.loadUniverse();
        //System.out.println(test1.universe);

        //TEST BFS
        //Graph<String, Set<String>> BFStest = BFSGraphLib.bfs(test.universe, test.currentCenter);
        //System.out.println(BFStest); //only connecting two : ISSUE W MY BFS
//        for (String v : BFStest.vertices()) {
//            System.out.println(v + ": inNeighbors" + BFStest.inDegree(v));
//        }

//        System.out.println(BFSGraphLib.missingVertices(test.universe, BFStest));
//
//        System.out.println(BFSGraphLib.averageSeparation(BFStest, "Kevin Bacon"));

        //List<String> BFStestNobody = BFSGraphLib.getPath(BFStest, "Nobody"); //my bfs path isnt working
//        for (String s : BFStestNobody) {
//            System.out.println(s);
//        }
        //this isnt working for some reason (im going to kill myself

        //List<String> BFStestDartmouth = BFSGraphLib.getPath(BFStest, "Dartmouth (Earl thereof)"); //my bfs path isnt working
//        for (String s : BFStestDartmouth) {
//            System.out.println(s);
//        }

//test.helperc(5);--> good
// test.helperd(2,4); --> good
//        test.helperd(4,2);
        //test.helperi();
        //test.helperp("Bob");
        //test.helperu("Nobody");

        //testfiles actual game ! it works with test files
    //test1.startgame("ps4/actorsTest.txt", "ps4/moviesTest.txt", "ps4/movie-actorsTest.txt");
       //movie map == movieactor map
        //actor map: 7

        //seeing issues with map in test2
        BaconGame<String, Set<String>> test2 = new BaconGame();
        test2.startgame("ps4/actors.txt", "ps4/movies.txt", "ps4/movie-actors.txt");

//should be 7172 movies and 7172 movie actors size
    }
}


