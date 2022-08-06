import java.io.BufferedWriter;
import java.io.FileWriter;
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
        final String apiKey = "<**API-KEY**";
        final String bearerToken = "<**BEARER**>";
        final String imdbId = "0361748";

//        int fileId = getFileId(apiKey, imdbId);
//        System.out.println(fileId);
//
//        String downloadLink = getDownloadLink(apiKey, fileId, bearerToken);
//        System.out.println(downloadLink);
//
//        String subtitles = useDownloadLink(downloadLink);
//        System.out.println(subtitles);

//        BufferedWriter writer = new BufferedWriter(new FileWriter("./test7.srt"));
//        writer.write(subtitles);
//
//        writer.close();

        Movie movie = new Movie();
        movie.setSubtitle();

        String subtitlesText = extractTextFromSubtitles(movie.getSubtitle());
//        String subtitlesText = extractTextFromSubtitles(subtitles);
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


//        System.out.println(fullSubtitles);
        fullSubtitles = fullSubtitles.replaceAll("\\<.*?\\>|\\[.*?\\]|\\{.*?\\}|\\(.*?\\)", "");
//        fullSubtitles = fullSubtitles.replaceAll("―|―|-||\"|,|!|“|”", "");
        fullSubtitles = fullSubtitles.replace("―", " ");
        fullSubtitles = fullSubtitles.replace("–", " ");
        fullSubtitles = fullSubtitles.replace("-", " ");
        fullSubtitles = fullSubtitles.replace("\"", "");
        fullSubtitles = fullSubtitles.replace("“", "");
        fullSubtitles = fullSubtitles.replace("”", "");
        fullSubtitles = fullSubtitles.replace(",", "");
        fullSubtitles = fullSubtitles.replace(".", "");
        fullSubtitles = fullSubtitles.replace("…", "");
        fullSubtitles = fullSubtitles.replace("♪", "");
        fullSubtitles = fullSubtitles.replace(":", "");
        fullSubtitles = fullSubtitles.replace(";", "");

        fullSubtitles = fullSubtitles.replace("?", "");
        fullSubtitles = fullSubtitles.replace("!", "");
        fullSubtitles = fullSubtitles.toLowerCase();

        BufferedWriter writer = new BufferedWriter(new FileWriter("./output2.txt"));
        writer.write(fullSubtitles);

        writer.close();

        fullSubtitles = fullSubtitles.replace("\n", " ");
        fullSubtitles = fullSubtitles.trim().replaceAll(" +", " "); // reduces multiple spaces to one

        String[] words = fullSubtitles.split(" ");
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
//        for (String key: hm2.keySet()) {
//            if (stopWords.contains(key)) {
//                hm2.remove(key);
//            }
//            System.out.println(key);

//        }

        hm2.entrySet().removeIf(k -> stopWords.contains(k.getKey()));

        System.out.println(hm2.entrySet());
        System.out.println(hm2.size());

        System.out.println();

        // phrases
        int phraseLength = 9;
        HashMap<String, Integer> phr = new HashMap<>();
        for (int i = 0; i < words.length - (phraseLength - 1); i++) {
            String phrase = "";
            if (phraseLength == 2) {
                phrase = format("%s %s", words[i], words[i+1]);
            } else if (phraseLength == 3) {
                phrase = format("%s %s %s", words[i], words[i+1], words[i+2]);
            } else if (phraseLength == 4) {
                phrase = format("%s %s %s %s", words[i], words[i+1], words[i+2], words[i+3]);
            } else if (phraseLength == 5) {
                phrase = format("%s %s %s %s %s", words[i], words[i+1], words[i+2], words[i+3], words[i+4]);
            } else if (phraseLength == 6) {
                phrase = format("%s %s %s %s %s %s", words[i], words[i+1], words[i+2], words[i+3], words[i+4], words[i+5]);
            } else if (phraseLength == 7) {
                phrase = format("%s %s %s %s %s %s %s", words[i], words[i+1], words[i+2], words[i+3], words[i+4], words[i+5], words[i+6]);
            } else if (phraseLength == 8) {
                phrase = format("%s %s %s %s %s %s %s %s", words[i], words[i+1], words[i+2], words[i+3], words[i+4], words[i+5], words[i+6], words[i+7]);
            } else if (phraseLength == 9) {
                phrase = format("%s %s %s %s %s %s %s %s %s", words[i], words[i+1], words[i+2], words[i+3], words[i+4], words[i+5], words[i+6], words[i+7], words[i+8]);
            }

            int value = 1;
            if (phr.containsKey(phrase)) {
                value += phr.get(phrase);
            }
            phr.put(phrase, value);
        }
//        System.out.println(phr.entrySet());

        phr = sortByValue(phr);

//        System.out.println(phr.entrySet());
        System.out.println(phr.size());

        return "";
    }


    private static String useDownloadLink(String downloadLink) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
//        client.followRedirects();
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

        HttpClient client = HttpClient.newHttpClient();
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

//        String responseBody = "{\"link\":\"https://www.opensubtitles" +
//                ".com/download" +
//                "/C5A69AE16086D3322A62E46545CB82D58D4FFBEAC3FBF7BE74E7E10D0F0DE60E46BEA55B4EDB9A414B40EBD84B825CEDA0363555E8DDFD7AA5AE958930249342F1AF4B3580C52495AD49592AA779A4378D109F2DFFEAF4DCF335415180F91E789B0685A02C18137F882FB938AD53C2F511D8D9C5D9D5308D8CC6EB4AB1F4E5B84AD2C1C459E441AFB79B24923DC739C00AC0DAE72F3644E13AB7979069E12303E23DEF215A4611621D49FFDE02EEE5628FD430F2F083DD2EBA4EA22B216B1E9E11DB4AFF550D48D6D2E07300F4518040C8A9CC4A80BD6B63AF428213A465E42429A6E2194A54C3F4E1A39C7A31C25575342EE22A2CBA84620575B2CB4B98D2FF056A378AB6D814A119272D884FC99AC2F57FAFFCBF3BA78D9310461A46A0DAA55B72314B781B0D4A/subfile/Avengers%20Endgame%202019%20NEW%20720p%20HDCAM%20x264-iMaX.srt\",\"file_name\":\"Avengers Endgame 2019 NEW 720p HDCAM x264-iMaX.srt\",\"requests\":5,\"remaining\":95,\"message\":\"Your quota will be renewed in 05 hours and 50 minutes (2022-08-05 15:01:36 UTC) \",\"reset_time\":\"05 hours and 50 minutes\",\"reset_time_utc\":\"2022-08-05T15:01:36.000Z\"}\n";

        int index = responseBody.indexOf("{\"link\":\"");

        int indexLinkStart = index + 9;
        int indexLinkEnd = responseBody.indexOf('\"', indexLinkStart);

        String downloadLink = responseBody.substring(indexLinkStart, indexLinkEnd);

        System.out.println(responseBody);

        return downloadLink;
    }

    private static int getFileId(String apiKey, String imdbId) throws IOException, InterruptedException {
//        HttpClient clientOld = HttpClient.newHttpClient();
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Api-Key", apiKey)
                .uri(URI.create(format("https://api.opensubtitles.com/api/v1/subtitles?imdb_id=%s", imdbId)))
//                .header("Content-type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client .send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if(response.statusCode() != 200) {
            System.out.println(format("Request failed with status code: %d", response.statusCode()));
        }

        String responseBody = response.body();

//        String responseBody = "{\"total_pages\":8,\"total_count\":463,\"per_page\":60,\"page\":1," +
//                "\"data\":[{\"id\":\"4819116\",\"type\":\"subtitle\",\"attributes\":{\"subtitle_id\":\"4819116\"," +
//                "\"language\":\"en\",\"download_count\":2005086,\"new_download_count\":3557," +
//                "\"hearing_impaired\":false,\"hd\":true,\"fps\":30.0,\"votes\":8,\"ratings\":5.8," +
//                "\"from_trusted\":true,\"foreign_parts_only\":false,\"upload_date\":\"2019-04-27T15:11:09Z\"," +
//                "\"ai_translated\":false,\"machine_translated\":false,\"release\":\"Avengers Endgame 2019 NEW 720p " +
//                "HDCAM x264-iMaX\",\"comments\":\"Runtime: 02:46:13 -- Subs by Fuj69Film. It's a cam transcript, so " +
//                "be gentle. improvements are welcome. Some dialogue between Hawkeye and daughter are missing.\"," +
//                "\"legacy_subtitle_id\":7733458,\"uploader\":{\"uploader_id\":82503,\"name\":\"scooby74\"," +
//                "\"rank\":\"trusted\"},\"feature_details\":{\"feature_id\":626618,\"feature_type\":\"Movie\"," +
//                "\"year\":2019,\"title\":\"Avengers: Endgame\",\"movie_name\":\"2019 - Avengers: Endgame\"," +
//                "\"imdb_id\":4154796,\"tmdb_id\":299534},\"url\":\"https://www.opensubtitles" +
//                ".com/en/subtitles/legacy/7733458\",\"related_links\":{\"label\":\"All subtitles for Avengers: " +
//                "Endgame\",\"url\":\"https://www.opensubtitles.com/en/movies/2019-untitled-avengers-movie\"," +
//                "\"img_url\":\"https://s9.osdb.link/features/8/1/6/626618.jpg\"},\"files\":[{\"file_id\":4942453," +
//                "\"cd_number\":1,\"file_name\":\"Avengers Endgame 2019 NEW 720p HDCAM x264-iMaX.srt\"}]}}," +
//                "{\"id\":\"4819440\",\"type\":\"subtitle\",\"attributes\":{\"subtitle_id\":\"4819440\",\"language\":\"";

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

    // function to sort hashmap by values
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
//        for (Map.Entry<String, Integer> aa : list) {
//            temp.put(aa.getKey(), aa.getValue());
//        }
        for (int i = list.size() - 1; i >= 0; i--) {
            Map.Entry<String, Integer> aa = list.get(i);
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


}
