import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    @BeforeEach
    public void setUp() throws IOException {
        Files.write(Paths.get("posts.txt"), new byte[0]);
        Files.write(Paths.get("comments.txt"), new byte[0]);
    }

    @Test
    public void testAddPostValid() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post1 = new Post(1, "This is a valid title", "A".repeat(300), tags, "Difficult", "Highly Needed");
        Post post2 = new Post(2, "Another valid title", "B".repeat(300), tags, "Ordinary", "Highly Needed");

        assertTrue(post1.addPost());
        assertTrue(post2.addPost());

        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        assertTrue(lines.contains(post1.toString()));
        assertTrue(lines.contains(post2.toString()));
    }

    @Test
    public void testAddPostInvalidTitle() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post1 = new Post(2, "Short", "A".repeat(300), tags, "Difficult", "Highly Needed");
        Post post2 = new Post(3, "", "A".repeat(300), tags, "Difficult", "Highly Needed");

        assertFalse(post1.addPost());
        assertFalse(post2.addPost());

        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        assertFalse(lines.contains(post1.toString()));
        assertFalse(lines.contains(post2.toString()));
    }

    @Test
    public void testAddPostInvalidBody() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post1 = new Post(3, "This is a valid title", "Short body", tags, "Difficult", "Highly Needed");
        Post post2 = new Post(4, "This is another valid title", "", tags, "Difficult", "Highly Needed");

        assertFalse(post1.addPost());
        assertFalse(post2.addPost());

        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        assertFalse(lines.contains(post1.toString()));
        assertFalse(lines.contains(post2.toString()));
    }

    @Test
    public void testAddPostInvalidTags() throws IOException {
        String[] tags1 = {"tag1"};
        String[] tags2 = {"Tag1", "tag2"};
        Post post1 = new Post(4, "This is a valid title", "A".repeat(300), tags1, "Difficult", "Highly Needed");
        Post post2 = new Post(5, "This is another valid title", "A".repeat(300), tags2, "Difficult", "Highly Needed");

        assertFalse(post1.addPost());
        assertFalse(post2.addPost());

        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        assertFalse(lines.contains(post1.toString()));
        assertFalse(lines.contains(post2.toString()));
    }

    @Test
    public void testAddPostInvalidEmergency() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post1 = new Post(6, "This is a valid title", "A".repeat(300), tags, "Difficult", "Invalid Emergency");
        Post post2 = new Post(7, "This is another valid title", "A".repeat(300), tags, "Difficult", "");

        assertFalse(post1.addPost());
        assertFalse(post2.addPost());

        List<String> lines = Files.readAllLines(Paths.get("posts.txt"));
        assertFalse(lines.contains(post1.toString()));
        assertFalse(lines.contains(post2.toString()));
    }

    @Test
    public void testAddCommentValid() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(7, "This is a valid title", "A".repeat(300), tags, "Difficult", "Highly Needed");
        post.addPost();

        assertTrue(post.addComment("This is a valid comment."));
        assertTrue(post.addComment("Another valid comment."));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertTrue(lines.contains("7,This is a valid comment."));
        assertTrue(lines.contains("7,Another valid comment."));
    }

    @Test
    public void testAddCommentInvalidShort() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(8, "This is a valid title", "A".repeat(300), tags, "Difficult", "Highly Needed");
        post.addPost();

        assertFalse(post.addComment("Too short."));
        assertFalse(post.addComment("Not enough."));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertFalse(lines.contains("8,Too short."));
        assertFalse(lines.contains("8,Not enough."));
    }

    @Test
    public void testAddCommentInvalidLong() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(9, "This is a valid title", "A".repeat(300), tags, "Difficult", "Highly Needed");
        post.addPost();

        assertFalse(post.addComment("This comment contains way too many words to be valid."));
        assertFalse(post.addComment("This comment also contains way too many words and should not be added."));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertFalse(lines.contains("9,This comment contains way too many words to be valid."));
        assertFalse(lines.contains("9,This comment also contains way too many words and should not be added."));
    }

    @Test
    public void testAddCommentInvalidFormat() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(10, "This is a valid title", "A".repeat(300), tags, "Difficult", "Highly Needed");
        post.addPost();

        assertFalse(post.addComment("this comment starts with a lowercase letter."));
        assertFalse(post.addComment("valid but"));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertFalse(lines.contains("10,this comment starts with a lowercase letter."));
        assertFalse(lines.contains("10,valid but"));
    }

    @Test
    public void testAddMultipleCommentsForEasyPost() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(11, "This is a valid title", "A".repeat(300), tags, "Easy", "Ordinary");
        post.addPost();

        assertTrue(post.addComment("Valid comment."));
        assertTrue(post.addComment("Another valid comment."));
        assertTrue(post.addComment("Yet another valid comment."));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertTrue(lines.contains("11,Valid comment."));
        assertTrue(lines.contains("11,Another valid comment."));
        assertTrue(lines.contains("11,Yet another valid comment."));
    }

    @Test
    public void testAddExceedingCommentsForEasyPost() throws IOException {
        String[] tags = {"tag1", "tag2"};
        Post post = new Post(12, "This is a valid title", "A".repeat(300), tags, "Easy", "Ordinary");
        post.addPost();

        post.addComment("Valid comment.");
        post.addComment("Another valid comment.");
        post.addComment("Yet another valid comment.");
        assertFalse(post.addComment("Exceeding comment limit."));

        List<String> lines = Files.readAllLines(Paths.get("comments.txt"));
        assertFalse(lines.contains("12,Exceeding comment limit."));
    }
}