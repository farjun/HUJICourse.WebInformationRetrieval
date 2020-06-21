import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import webdata.IndexReader;
import webdata.IndexWriter;
import webdata.ReviewSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchTest {

	final static String indexDir = "./index\\";
	final static String inputFile = "./datasets/counted/1000.txt";
	private static ReviewSearch search;

	@BeforeAll
	static void before() {
		IndexWriter indexWriter = new IndexWriter();
		indexWriter.write(inputFile, indexDir);
		search = new ReviewSearch(new IndexReader(indexDir));

	}


	@Test
	void TestVectorSpaceSearchQuery1() {
		ArrayList<String> query = new ArrayList<>();
		query.add("flavored");
		query.add("coffee");

		// top 5
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),5);
		int[] vectorSpaceSearchResults = {83, 371, 314, 315, 316};
		assertEnumerationArrayEquals(vectorSpaceSearchActual, vectorSpaceSearchResults);

		// top 10
		Enumeration<Integer> vectorSpaceSearchActual2 =
				search.vectorSpaceSearch(Collections.enumeration(query),10);
		int[] vectorSpaceSearchResults2 = {83, 371, 314, 315, 316, 670, 767, 978, 975, 979};
		assertEnumerationArrayEquals(vectorSpaceSearchActual2, vectorSpaceSearchResults2);
	}

	@Test
	void TestVectorSpaceSearchQuery2() {
		ArrayList<String> query = new ArrayList<>();
		query.add("addition");
		query.add("ZuCchini");
		query.add("beer");

		// top 5
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),5);
		int[] vectorSpaceSearchResults = {4, 902, 904, 468, 603};
		assertEnumerationArrayEquals(vectorSpaceSearchActual, vectorSpaceSearchResults);

		// top 10
		Enumeration<Integer> vectorSpaceSearchActual2 =
				search.vectorSpaceSearch(Collections.enumeration(query),10);
		int[] vectorSpaceSearchResults2 = {4, 902, 904, 468, 603, 932, 942, 944, 357, 498};
		assertEnumerationArrayEquals(vectorSpaceSearchActual2, vectorSpaceSearchResults2);
	}

	@Test
	void TestVectorSpaceSearchQuery3() {
		ArrayList<String> query = new ArrayList<>();
		query.add("ZuCchini");

		// top 5
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),5);
		int[] vectorSpaceSearchResults = {902, 932, 942, 944};
		assertEnumerationArrayEquals(vectorSpaceSearchActual, vectorSpaceSearchResults);
	}

	@Test
	void TestVectorSpaceSearchQuery4() {
		ArrayList<String> query = new ArrayList<>();
		query.add("love");
		query.add("candy");

		// top 10
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),10);
		int[] vectorSpaceSearchResults = {783, 24, 57, 72, 7, 62, 204, 19, 20, 23};
		assertEnumerationArrayEquals(vectorSpaceSearchActual, vectorSpaceSearchResults);
	}

	@Test
	void TestVectorSpaceSearchQuery5() {
		ArrayList<String> query = new ArrayList<>();
		query.add("love");
		query.add("love");
		query.add("love");
		query.add("candy");

		// top 10
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),10);
		int[] vectorSpaceSearchResults = {24, 57, 72, 783, 7, 62, 204, 284, 345, 210};
		assertEnumerationArrayEquals(vectorSpaceSearchActual, vectorSpaceSearchResults);
	}


	@Test
	void TestLanguageModelSearchQuery1() {
		ArrayList<String> query = new ArrayList<>();
		query.add("flavored");
		query.add("coffee");

		// top 5
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.4,5);
		int[] languageModelSearchResults = {670, 315, 314, 316, 620};
		assertEnumerationArrayEquals(languageModelSearchActual, languageModelSearchResults);

		// top 10
		Enumeration<Integer> languageModelSearchActual2 =
				search.languageModelSearch(Collections.enumeration(query),0.4,10);
		int[] languageModelSearchResults2 = {670, 315, 314, 316, 620, 446, 950, 439, 83, 371};
		assertEnumerationArrayEquals(languageModelSearchActual2, languageModelSearchResults2);


		// top 10 different lambda
		Enumeration<Integer> languageModelSearchActual3 =
				search.languageModelSearch(Collections.enumeration(query),0.5,10);
		int[] languageModelSearchResults3 = {670, 315, 314, 316, 620, 83, 371, 446, 950, 439};
		assertEnumerationArrayEquals(languageModelSearchActual3, languageModelSearchResults3);
	}

	@Test
	void TestLanguageModelSearchQuery2() {
		ArrayList<String> query = new ArrayList<>();
		query.add("addition");
		query.add("ZuCchini");
		query.add("beer");

		// top 5
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.6,5);
		int[] languageModelSearchResults = {4, 778, 902, 942, 756};
		assertEnumerationArrayEquals(languageModelSearchActual, languageModelSearchResults);

		// top 10
		Enumeration<Integer> languageModelSearchActual2 =
				search.languageModelSearch(Collections.enumeration(query),0.6,10);
		int[] languageModelSearchResults2 = {4, 778, 902, 942, 756, 794, 944, 904, 932, 6};
		assertEnumerationArrayEquals(languageModelSearchActual2, languageModelSearchResults2);


		// top 10 different lambda
		Enumeration<Integer> languageModelSearchActual3 =
				search.languageModelSearch(Collections.enumeration(query),0.3,10);
		int[] languageModelSearchResults3 = {4, 778, 902, 942, 756, 794, 944, 904, 932, 6};
		assertEnumerationArrayEquals(languageModelSearchActual3, languageModelSearchResults3);
	}

	@Test
	void TestLanguageModelSearchQuery3() {
		ArrayList<String> query = new ArrayList<>();
		query.add("ZuCchini");

		// top 5
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.6,5);
		int[] languageModelSearchResults = {902, 942, 944, 932};
		assertEnumerationArrayEquals(languageModelSearchActual, languageModelSearchResults);

		// top 5 different lambda
		Enumeration<Integer> languageModelSearchActual3 =
				search.languageModelSearch(Collections.enumeration(query),0.3,5);
		int[] languageModelSearchResults3 = {902, 942, 944, 932};
		assertEnumerationArrayEquals(languageModelSearchActual3, languageModelSearchResults3);
	}

	@Test
	void TestLanguageModelSearchQuery4() {
		ArrayList<String> query = new ArrayList<>();
		query.add("love");
		query.add("candy");

		// top 10
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.5,10);
		int[] languageModelSearchResults = {24, 57, 62, 950, 27, 7, 727, 20, 23, 72};
		assertEnumerationArrayEquals(languageModelSearchActual, languageModelSearchResults);

	}

	@Test
	void TestLanguageModelSearchQuery5() {
		ArrayList<String> query = new ArrayList<>();
		query.add("love");
		query.add("love");
		query.add("love");
		query.add("candy");

		// top 10
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.5,10);
		int[] languageModelSearchResults = {24, 57, 14, 446, 454, 706, 453, 488, 580, 703};
		assertEnumerationArrayEquals(languageModelSearchActual, languageModelSearchResults);

	}


	@Test
	void NoneResultSearchTest(){
		ArrayList<String> query = new ArrayList<>();
		query.add("Sagiv");
		query.add("Shani");

		int[] results = {};

		// top 5
		Enumeration<Integer> vectorSpaceSearchActual =
				search.vectorSpaceSearch(Collections.enumeration(query),5);
		assertEnumerationArrayEquals(vectorSpaceSearchActual, results);

		// top 5
		Enumeration<Integer> languageModelSearchActual =
				search.languageModelSearch(Collections.enumeration(query),0.5,5);
		assertEnumerationArrayEquals(languageModelSearchActual, results);

	}



	private void assertEnumerationArrayEquals(Enumeration<Integer> actualEnumeration, int[] resultArray) {
		ArrayList<Integer> arr = Collections.list(actualEnumeration);
//		System.out.println(arr); // print actual for debug!.
		int[] actualArray = arr.stream().mapToInt(Integer::intValue).toArray();
		assertArrayEquals(resultArray, actualArray);
	}

}
