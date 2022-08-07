import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Movie {
    private String imdbId;
    private String resultType;
    private String imgLink;
    private String title;
    private String description;


    private String subtitle;

    public Movie(String imdbId, String resultType, String imgLink, String title, String description) {
        this.imdbId = imdbId;
        this.resultType = resultType;
        this.imgLink = imgLink;
        this.title = title;
        this.description = description;

    }

    public String getImdbId() {
        return this.imdbId;
    }

    public String getResultType() {
        return this.resultType;
    }

    public String getImgLink() {
        return this.imgLink;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }



    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle() throws IOException {
        String s = new String(Files.readAllBytes(Paths.get("./test9.srt")));
        s = s.replaceAll("\r", "");

        this.subtitle = s;
    }
}
