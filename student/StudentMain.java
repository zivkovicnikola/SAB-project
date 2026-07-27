package student;

import rs.ac.bg.etf.sab.operations.*;
import rs.ac.bg.etf.sab.tests.TestHandler;
import rs.ac.bg.etf.sab.tests.TestRunner;

public class StudentMain {

    public static void main(String[] args) throws Exception {

        GeneralOperations generalOperations = new zn230352_GeneralOperations();
        GenresOperations genresOperations = new zn230352_GenresOperations();
        MoviesOperations moviesOperations = new zn230352_MoviesOperations();
        RatingsOperations ratingsOperation = new zn230352_RatingsOperations();
        TagsOperations tagsOperations = new zn230352_TagsOperations();
        UsersOperations usersOperations = new zn230352_UsersOperations();
        WatchlistsOperations watchlistsOperations = new zn230352_WatchlistsOperations();

        TestHandler.createInstance(
                genresOperations,
                moviesOperations,
                ratingsOperation,
                tagsOperations,
                usersOperations,
                watchlistsOperations,
                generalOperations);
        TestRunner.runTests();


    }
}