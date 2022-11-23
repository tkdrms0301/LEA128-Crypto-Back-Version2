package kumoh.util.hash;

public interface Hash {
    String getHash();
    int getHashLength();

    void setHash(String input, String type);
}
