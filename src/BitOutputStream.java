import java.io.IOException;
import java.io.OutputStream;
class BitOutputStream implements AutoCloseable {
    private OutputStream out;
    private int currentByte;
    private int numBitsInCurrentByte;

    public BitOutputStream(OutputStream out) {
        this.out = out;
        currentByte = 0;
        numBitsInCurrentByte = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Argument must be 0 or 1");
        }
        currentByte = (currentByte << 1) | bit;
        numBitsInCurrentByte++;
        if (numBitsInCurrentByte == 8) {
            flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (numBitsInCurrentByte > 0) {
            currentByte <<= (8 - numBitsInCurrentByte);
            out.write(currentByte);
        }
        out.close();
    }

    private void flush() throws IOException {
        if (numBitsInCurrentByte == 8) {
            out.write(currentByte);
            currentByte = 0;
            numBitsInCurrentByte = 0;
        }
    }
}
