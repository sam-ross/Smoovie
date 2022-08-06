import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Main {
    private static final String BASE_URL = "https://api.opensubtitles.com/api/v1";

    public static void main(String[] args) throws IOException, InterruptedException {
        final String apiKey = "<API-KEY>";
        final String bearerToken = "<BEARER TOKEN>";
        final String imdbId = "0361748";

        int fileId = getFileId(apiKey, imdbId);
        System.out.println(fileId);

        String downloadLink = getDownloadLink(apiKey, fileId, bearerToken);
        System.out.println(downloadLink);

        String subtitles = useDownloadLink(downloadLink);
        System.out.println(subtitles);

//        BufferedWriter writer = new BufferedWriter(new FileWriter("./test7.srt"));
//        writer.write(subtitles);
//
//        writer.close();

//        Movie movie = new Movie();
//        movie.setSubtitle();
//
//        String subtitlesText = extractTextFromSubtitles(movie.getSubtitle());
        String subtitlesText = extractTextFromSubtitles(subtitles);
    }

    private static String extractTextFromSubtitles(String subtitles) throws IOException {
        Matcher matcher = Pattern.compile("(?<order>\\d+)\\n(?<start>[\\d:,]+)\\s+-{2}\\>\\s+(?<end>[\\d:,]+)\\n" +
                "(?<text>[\\s\\S]*?(?=\\n{2}|$))").matcher(subtitles);

//        subtitles = subtitles.replaceAll("\r", "");

        StringBuilder fullSubtitlesBuilder = new StringBuilder();
        while(matcher.find()) {
            fullSubtitlesBuilder.append("\n" + matcher.group("text"));
        }

        // start check
        int endOfFirstLine = fullSubtitlesBuilder.indexOf("\n", 1);
        int endOfSecondLine = fullSubtitlesBuilder.indexOf("\n", endOfFirstLine + 1);
        String start = fullSubtitlesBuilder.substring(0, endOfSecondLine).toLowerCase();

        if (start.contains("sub") || start.contains("caption") || start.contains("credit") || start.contains("sync")) {
            fullSubtitlesBuilder.delete(0, endOfSecondLine + 1);
        }

        // end check
        int endOfSecondLastLine = fullSubtitlesBuilder.lastIndexOf("\n");
        int endOfThirdLastLine = fullSubtitlesBuilder.substring(0, endOfSecondLastLine).lastIndexOf("\n");
        String end = fullSubtitlesBuilder.substring(endOfThirdLastLine, fullSubtitlesBuilder.length()).toLowerCase();

        if (end.contains("sub") || end.contains("caption") || end.contains("credit") || end.contains("sync")) {
            fullSubtitlesBuilder.delete(endOfThirdLastLine, fullSubtitlesBuilder.length());
        }

        String fullSubtitles = fullSubtitlesBuilder.toString();


        fullSubtitles = fullSubtitles.replaceAll("\\<.*?\\>|\\[.*?\\]|\\{.*?\\}|\\(.*?\\)", "");
        fullSubtitles = fullSubtitles.replace("―", " ").replace("–", " ").replace("-", " ");

        List<String> charactersToRemove = List.of("\"", "“", "”", ",", ".", "…", "♪", ":", ";", "?", "!");
        for (String ch: charactersToRemove) {
            fullSubtitles = fullSubtitles.replace(ch, "");
        }

        fullSubtitles = fullSubtitles.toLowerCase();

//        BufferedWriter writer = new BufferedWriter(new FileWriter("./output2.txt"));
//        writer.write(fullSubtitles);
//
//        writer.close();

        fullSubtitles = fullSubtitles.replace("\n", " ");
        fullSubtitles = fullSubtitles.trim().replaceAll(" +", " "); // reduces multiple spaces to one

        String[] wordsArr = fullSubtitles.split(" ");
        List<String> words = List.of(wordsArr);
        for (String word: words) {
            System.out.println(format("\"%s\"", word));
        }

        // words
        HashMap<String, Integer> hm = new HashMap<>();
        for (String word: words) {
            int value = 1;
            if (hm.containsKey(word)) {
                value += hm.get(word);
            }
            hm.put(word, value);
        }
        System.out.println(hm.entrySet());

        hm = sortByValue(hm);

        System.out.println(hm.entrySet());
        System.out.println(hm.size());

        System.out.println();

        // remove stopwords
        HashMap<String, Integer> hm2 = hm;
        List<String> stopWords = Utils.getStopWords();

        hm2.entrySet().removeIf(k -> stopWords.contains(k.getKey()));

        System.out.println(hm2.entrySet());
        System.out.println(hm2.size());

        System.out.println();

        // phrases
        int phraseLength = 10;
        HashMap<String, Integer> phr = new HashMap<>();
        int numberOfDuplicates = 0;

        // O(N) improved version
        for (int i = 0; i < words.size() - (phraseLength - 1); i++) {
//            String phrase = words.get(i);
//
//            for (int j = 1; j < phraseLength; j++) {
//                phrase += format(" %s", words.get(i + j));
//            }

            String phrase = String.join(" ", words.subList(i, i + phraseLength));   // indexTo is exclusive

            int value = 1;
            if (phr.containsKey(phrase)) {
                value += phr.get(phrase);
                numberOfDuplicates++;
            }
            phr.put(phrase, value);
        }
//        System.out.println(phr.entrySet());

        if (numberOfDuplicates == 0) System.out.println("There were no common phrases for this size of phrase");

        phr = sortByValue(phr);

        System.out.println(phr.values());
        System.out.println(phr.entrySet());
        System.out.println(phr.size());

//        BufferedWriter writer = new BufferedWriter(new FileWriter("./disbelief.txt"));
//        writer.write(phr.entrySet().toString());
//
//        writer.close();

        return "";
    }


    private static String useDownloadLink(String downloadLink) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadLink))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println(response.statusCode());
        String responseBody = response.body();

        return responseBody;
    }

    private static String getDownloadLink(
            String apiKey,
            int fileId,
            String bearerToken
    ) throws IOException, InterruptedException {
        String requestBody = format("{\"file_id\": %d}", fileId);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.opensubtitles.com/api/v1/download"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Authorization", format("Bearer %s", bearerToken))
                .header("Api-Key", apiKey)
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        String responseBody = response.body();

        int index = responseBody.indexOf("{\"link\":\"");

        int indexLinkStart = index + 9;
        int indexLinkEnd = responseBody.indexOf('\"', indexLinkStart);

        String downloadLink = responseBody.substring(indexLinkStart, indexLinkEnd);

        System.out.println(responseBody);

        return downloadLink;
    }

    private static int getFileId(String apiKey, String imdbId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Api-Key", apiKey)
                .uri(URI.create(format("https://api.opensubtitles.com/api/v1/subtitles?imdb_id=%s", imdbId)))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client .send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        String responseBody = response.body();

        int pos = responseBody.indexOf("\"files\":[{\"file_id\":");
        int startOfId = pos + 20;
        int endOfId = responseBody.indexOf(',', startOfId);

        System.out.println("length: " + responseBody.length());

        System.out.println("pos: " + pos);
        System.out.println("posStart: " + startOfId);
        System.out.println("posEnd: " + endOfId);
        System.out.println("substring: " + responseBody.substring(startOfId, endOfId));

        String fileId = responseBody.substring(startOfId, endOfId);
        return Integer.parseInt(fileId);
    }

    // method to sort hashmap by values
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list
                = new LinkedList<Map.Entry<String, Integer> >(
                hm.entrySet());

        // Sort the list using lambda expression
        Collections.sort(
                list,
                Comparator.comparing(Map.Entry::getValue));

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp
                = new LinkedHashMap<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            Map.Entry<String, Integer> aa = list.get(i);
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
