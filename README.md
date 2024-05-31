# Textfile Compression
This project implements text file compression using Huffman coding in Java. 
## Usage

### To use this code:

    1. Ensure you have Java installed on your system.
    2. Compile the HuffmanCode.java file using a Java compiler.
    3. Execute the compiled Java class.

### Example Usage:

```java
// Define input and output file paths
String inputFilePath = "input.txt";
String compressedFilePath = "compressed.bin";
String decompressedFilePath = "decompressed.txt";

// Compress the input file
HuffmanCode.compressFile(inputFilePath, compressedFilePath);

// Decompress the compressed file
HuffmanCode.decompressFile(compressedFilePath, decompressedFilePath);

// Display compression statistics
HuffmanCode.stats(inputFilePath, compressedFilePath);

```