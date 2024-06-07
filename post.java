import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Post {
    private int id;
    private String title;
    private String body;
    private String[] tags;
    private String difficulty;
    private String emergency;
    private int commentCount;

    public Post(int id, String title, String body, String[] tags, String difficulty, String emergency) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.difficulty = difficulty;
        this.emergency = emergency;
        this.commentCount = 0;
    }

    // Method to add a post to the file
    public boolean addPost() throws IOException {
        if (!isValidTitle() || !isValidBody() || !isValidTags() || !isValidEmergency()) {
            return false;
        }

        String postLine = id + "," + title + "," + body + "," + String.join(" ", tags) + "," + difficulty + "," + emergency + "," + commentCount + "\n";
        Files.write(Paths.get("posts.txt"), postLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        return true;
    }

    // Method to add a comment to the file
    public boolean addComment(String comment) throws IOException {
        if (!isValidComment(comment) || exceedsCommentLimit()) {
            return false;
        }

        String commentLine = id + "," + comment + "\n";
        Files.write(Paths.get("comments.txt"), commentLine.getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        incrementCommentCount();
        return true;
    }

    // Method to increment comment count for a post
    private void incrementCommentCount() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (Integer.parseInt(parts[0]) == this.id) {
                int currentCount = Integer.parseInt(parts[6]);
                parts[6] = String.valueOf(currentCount + 1);
                lines.set(i, String.join(",", parts));
                break;
            }
        }
        Files.write(Paths.get("posts.txt"), String.join("\n", lines).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    private boolean isValidTitle() {
        return title != null && title.length() > 10;
    }

    private boolean isValidBody() {
        return body != null && body.length() >= 300;
    }

    private boolean isValidTags() {
        if (tags == null || tags.length < 2) return false;
        for (String tag : tags) {
            if (Character.isUpperCase(tag.charAt(0))) return false;
        }
        return true;
    }

    private boolean isValidEmergency() {
        return "Highly Needed".equals(emergency) || "Ordinary".equals(emergency);
    }

    private boolean isValidComment(String comment) {
        String[] words = comment.split("\\s+");
        return words.length >= 4 && words.length <= 10 && Character.isUpperCase(words[0].charAt(0));
    }

    private boolean exceedsCommentLimit() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (Integer.parseInt(parts[0]) == this.id) {
                int currentCount = Integer.parseInt(parts[6]);
                if ("Easy".equals(difficulty) || "Ordinary".equals(difficulty)) {
                    return currentCount >= 3;
                } else {
                    return currentCount >= 5;
                }
            }
        }
        return false;
    }
}