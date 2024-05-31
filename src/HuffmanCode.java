import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HuffmanCode {
    public static void createHuffmanTree(String text, Map<Character, String> huffmanCode) {
        if (text == null || text.length() == 0) {
            return;
        }

        // Calculating the frequency of each character
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        // Create priority queue and add all nodes to it
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (var entry : freq.entrySet()) {
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }

        // Build Huffman tree
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.freq + right.freq;
            pq.add(new Node(null, sum, left, right));
        }

        // Root of the Huffman tree
        Node root = pq.peek();

        // Encode the data
        encodeData(root, "", huffmanCode);
    }

    // Encode data: 
    public static void encodeData(Node root, String str, Map<Character, String> huffmanCode) {
        if (root == null) {
            return;
        }

        if (root.isLeaf()) {
            huffmanCode.put(root.ch, str.length() > 0 ? str : "1");
        }

        encodeData(root.left, str + '0', huffmanCode);
        encodeData(root.right, str + '1', huffmanCode);
    }

    // Decode the encoded data:
    public static String decodeData(Node root, StringBuilder encodedString) {
        StringBuilder decodedString = new StringBuilder();
        Node current = root;
        for (int i = 0; i < encodedString.length(); i++) {
            current = (encodedString.charAt(i) == '0') ? current.left : current.right;

            if (current != null && current.isLeaf()) {
                decodedString.append(current.ch);
                current = root;
            }
        }
        return decodedString.toString();
    }

    // Compress the input text file and write the encoded data to the output file
    public static void compressFile(String inputFilePath, String outputFilePath) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        Map<Character, String> huffmanCode = new HashMap<>();
        StringBuilder encodedString = new StringBuilder();
        createHuffmanTree(text, huffmanCode);

        // Encode the data
        for (char c : text.toCharArray()) {
            encodedString.append(huffmanCode.get(c));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFilePath))) {
            oos.writeObject(huffmanCode);
            try (BitOutputStream bitOutputStream = new BitOutputStream(oos)) {
                for (char bit : encodedString.toString().toCharArray()) {
                    bitOutputStream.writeBit(bit == '1' ? 1 : 0);
                }
            }
        }
    }

    public static void decompressFile(String inputFilePath, String outputFilePath) throws IOException, ClassNotFoundException {
        Map<Character, String> huffmanCode;
        StringBuilder encodedString = new StringBuilder();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFilePath))) {
            huffmanCode = (Map<Character, String>) ois.readObject();
            try (BitInputStream bitInputStream = new BitInputStream(ois)) {
                int bit;
                while ((bit = bitInputStream.readBit()) != -1) {
                    encodedString.append(bit == 1 ? '1' : '0');
                }
            }
        }

        Node root = buildTreeFromCode(huffmanCode);
        String decodedString = decodeData(root, encodedString);

        Files.write(Paths.get(outputFilePath), decodedString.getBytes());
    }

    private static Node buildTreeFromCode(Map<Character, String> huffmanCode) {
        Node root = new Node(null, 0);
        for (Map.Entry<Character, String> entry : huffmanCode.entrySet()) {
            Node current = root;
            String code = entry.getValue();
            for (char bit : code.toCharArray()) {
                if (bit == '0') {
                    if (current.left == null) {
                        current.left = new Node(null, 0);
                    }
                    current = current.left;
                } else {
                    if (current.right == null) {
                        current.right = new Node(null, 0);
                    }
                    current = current.right;
                }
            }
            current.ch = entry.getKey();
        }
        return root;
    }

    public static void stats(String uncompressedFilePath, String compressedFilePath){
        File uncompressedFile = new File(uncompressedFilePath);
        File compressedFile = new File(compressedFilePath);
        // The compression ratio is the ratio of the number of bytes in the uncompressed representation 
        // to the number of bytes in the compressed representation.
        double uncompressedFileSizeMB = uncompressedFile.length() / Math.pow(10, 6);
        double compressedFileSizeMB = compressedFile.length() / Math.pow(10, 6);
        System.out.println("Size of Uncompressed File is : " + uncompressedFileSizeMB + "MB");
        System.out.println("Size of Compressed File is : " + compressedFileSizeMB + "MB");
        System.out.println("Compression Ratio is: " + compressedFileSizeMB / uncompressedFileSizeMB);
    }

    public static void main(String[] args) {
        try {
            String inputFilePath = "/home/saivinay/Desktop/textfile-compression/test_files/test.txt"; 
            String compressedFilePath = "/home/saivinay/Desktop/textfile-compression/test_files/compressed.txt"; 
            String decompressedFilePath = "/home/saivinay/Desktop/textfile-compression/test_files/decompressed.txt";

            compressFile(inputFilePath, compressedFilePath);
            decompressFile(compressedFilePath, decompressedFilePath);
            stats(inputFilePath, compressedFilePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}



