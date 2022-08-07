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
    private static final String OPEN_SUBTITLES_BASE_URL = "https://api.opensubtitles.com/api/v1";

    public static void main(String[] args) throws IOException, InterruptedException {
        final String apiKeyImdb = "XXX";
        final String movieNameInput = "lock stock";

        final String apiKey = "XXX";
        final String username = "XXX";
        final String password = "XXX";

        String imdbId = searchIMDbMovie(apiKeyImdb, movieNameInput);

        final String bearerToken = openSubtitlesLogIn(apiKey, username, password);
        System.out.println(bearerToken);

        int fileId = getFileId(apiKey, imdbId);
        System.out.println(fileId);

//        String downloadLink = getDownloadLink(apiKey, fileId, bearerToken);
        String downloadLink = getDownloadLink(apiKey, fileId);    // new

        System.out.println(downloadLink);

        String subtitles = useDownloadLink(downloadLink);
        System.out.println(subtitles);
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter("./test9.srt"));    //
//        writer.write(subtitles);  //
//
//        writer.close();   //

//        Movie movie = new Movie("", "", "", "", "");    //
//        movie.setSubtitle();  //
//
//        String subtitlesText = extractTextFromSubtitles(movie.getSubtitle()); //

        String subtitlesText = extractTextFromSubtitles(subtitles);
    }

    private static String openSubtitlesLogIn(String apiKey, String username, String password) throws IOException,
            InterruptedException {
        String requestBody = format("{\"password\": \"%s\", \"username\": \"%s\"}", password, username);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/login"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Api-Key", apiKey)
                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        String responseBody = response.body();
        System.out.println(response.statusCode());

        System.out.println(responseBody);

        int pos = responseBody.indexOf(",\"token\":\"");
        int startOfId = pos + 10;
        int endOfId = responseBody.indexOf('\"', startOfId);

        String bearerToken = responseBody.substring(startOfId, endOfId);

        return bearerToken;
    }

    private static String searchIMDbMovie(
            String apiKeyImdb,
            String movieNameInput
    ) throws IOException, InterruptedException {
        String convertedMovieName = movieNameInput.replace(" ", "%20");
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(format("https://imdb-api.com/en/API/SearchMovie/%s/%s", apiKeyImdb, convertedMovieName)))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        String responseBody = response.body();

        System.out.println(responseBody);

        int pos = responseBody.indexOf("[{\"id\":\"tt");
        int startOfId = pos + 10;
        int endOfId = responseBody.indexOf('\"', startOfId);
//        System.out.println(responseBody.charAt(pos + 10));

        String imdbId = responseBody.substring(startOfId, endOfId);

        return imdbId;

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

        List<String> charactersToRemove = List.of("\"", "“", "”", ",", ".", "…", "♪", ":", ";", "?", "!", "[", "]", "(", ")", "<", ">", "{", "}");
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

        hm = sortByValue(hm, false);

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
        int phraseLength = 3;
        HashMap<String, Integer> phr = new HashMap<>();
        int numberOfDuplicates = 0;

        // O(N) improved version
        for (int i = 0; i < words.size() - (phraseLength - 1); i++) {
            String phrase = String.join(" ", words.subList(i, i + phraseLength));   // indexTo is exclusive

            int value = 1;
            if (phr.containsKey(phrase)) {
                value += phr.get(phrase);
                numberOfDuplicates++;
            }
            phr.put(phrase, value);
        }

        if (numberOfDuplicates == 0) System.out.println("There were no common phrases for this size of phrase");

        phr = sortByValue(phr, true);

        System.out.println(phr.values());
        System.out.println(phr.entrySet());
        System.out.println(phr.size());

        // swear words
        List<String> swearWords = swearWords();
        List<String> easilyMistakenSwearWords = explicityDefinedSwearWords();   // these are swear words which could get mistaken for other words if using the contains method
        HashMap<String, Integer> swr = new HashMap<>();

        for (String key : hm.keySet()) {
            boolean foundSwearWord = false;
            for (String swearWord: swearWords) {
                if (key.contains(swearWord)) {
                    if (swr.get(swearWord) != null) {
                        swr.put(swearWord, swr.get(swearWord) + hm.get(key));
                    } else {
                        swr.put(swearWord, hm.get(key));
                    }
                    foundSwearWord = true;
                    break;
                }
            }
            if (!foundSwearWord && easilyMistakenSwearWords.contains(key)) {
                int existingValue = 0;
                if (swr.get(key) != null) {
                    existingValue = swr.get(key);
                }
                swr.put(key, existingValue + hm.get(key));
            }
        }

        swr = sortByValue(swr, false);

        System.out.println(swr.entrySet());

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
            int fileId
//            String bearerToken
    ) throws IOException, InterruptedException {
        String requestBody = format("{\"file_id\": %d}", fileId);

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/download"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .header("Authorization", format("Bearer %s", bearerToken))
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
                .uri(URI.create(OPEN_SUBTITLES_BASE_URL + "/subtitles?languages=en&imdb_id=" + imdbId))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            System.out.println(format("Unexpected status code: %d", response.statusCode()));
        }

        String responseBody = response.body();

        // need to check that a movie is definitely returned from imdb

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
    private static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm, boolean removeSingulars)
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
            if (removeSingulars) {
                if (aa.getValue() == 1) continue;
            }
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static List<String> swearWords() {
        return List.of(
                "arsehole",
                "bastard",
                "ballsack",
                "bellend",
                "bitch",
                "blowjob",
                "bollock",
                "boner",
                "boob",
                "bugger",
                "cocksucker",
                "cunt",
                "dildo",
                "dyke",
                "goddamn",
                "fanny",
                "fuck",
                "fudgepacker",
                "knobend",
                "nigg",
                "penis",
                "pussy",
                "shit",
                "tosser",
                "wanker",
                "whore"
        );
    }

    private static List<String> explicityDefinedSwearWords() {
        return List.of(
                "arse",
                "ass",
                "bloody",
                "bum",
                "butt",
                "buttplug",
                "cock",
                "coon",
                "crap",
                "dick",
                "dickhead",
                "fag",
                "faggot",
                "hell",
                "ho",
                "hoe",
                "homo",
                "piss",
                "poop",
                "prick",
                "queer",
                "sex",
                "slut",
                "vagina",
                "wank",
                "tit",
                "tits"
        );
    }
}
