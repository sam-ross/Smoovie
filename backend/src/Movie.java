import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Movie {
    private String subtitle;
    public Movie() {
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle() throws IOException {
        String s = new String(Files.readAllBytes(Paths.get("./test8.srt")));
        s = s.replaceAll("\r", "");

        this.subtitle = s;
    }

}
