//
// OpenSourceWrapped.java
//
// This file demonstrates how to embed an open‑source (public domain) code snippet
// inside your own custom Java code. The open‑source section below is a Java port
// of MurmurHash3 (original algorithm by Austin Appleby, placed in the public domain).
//
// The snippet is clearly delimited with markers:
//   >>> BEGIN OPEN‑SOURCE SNIPPET (PUBLIC DOMAIN)
//   >>> END OPEN‑SOURCE SNIPPET
//
// You may use, copy, modify, and distribute the public‑domain portion freely.
// As a courtesy, we include attribution to the original author.
//
// Compile:
//   javac OpenSourceWrapped.java
// Run:
//   java OpenSourceWrapped "some text"
//

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class OpenSourceWrapped {

    // ---------------------------
    // Custom application wrapper
    // ---------------------------
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java OpenSourceWrapped <text to hash>");
            System.out.println("Example: java OpenSourceWrapped \"Hello, world!\"");
            return;
        }

        String input = String.join(" ", args);
        byte[] data = input.getBytes(StandardCharsets.UTF_8);

        // Use the open‑source MurmurHash3 x64_128 implementation (below)
        long[] h = MurmurHash3_x64_128.hash(data, 0, data.length, 0);

        System.out.println("Input : " + input);
        System.out.println("MurmurHash3 x64_128:");
        System.out.printf("  h1 = 0x%016X%n", h[0]);
        System.out.printf("  h2 = 0x%016X%n", h[1]);

        // Example of how you might "surround" the snippet with custom logic:
        // e.g., simple integrity tag format with prefix/suffix, ready for log correlation.
        String integrityTag = "TAG[" + toHex(h) + "]";
        System.out.println("IntegrityTag: " + integrityTag);
    }

    private static String toHex(long[] pair) {
        StringBuilder sb = new StringBuilder(32);
        for (long v : pair) {
            sb.append(String.format("%016X", v));
        }
        return sb.toString();
    }

    // =====================================================================
    // >>> BEGIN OPEN‑SOURCE SNIPPET (PUBLIC DOMAIN): MurmurHash3 x64_128 <<<
    //
    // The MurmurHash3 algorithm was created by Austin Appleby and released
    // into the public domain. This Java implementation is a straightforward
    // port intended to match the reference behavior.
    //
    // Original author: Austin Appleby (public domain)
    // Java port/formatting for this file by: ChatGPT (public domain)
    //
    // Notes:
    //  - 128‑bit variant for 64‑bit platforms (x64_128)
    //  - Byte order: little‑endian
    //  - Seed supported (int/long), here we use a long seed for convenience
    //  - Returns two 64‑bit values (h1, h2)
    //
    // Reference (algorithm description & test vectors):
    //   https://github.com/aappleby/smhasher (public domain repository)
    // =====================================================================
    public static final class MurmurHash3_x64_128 {
        private MurmurHash3_x64_128() {}

        // Constants for x64_128
        private static final long C1 = 0x87c37b91114253d5L;
        private static final long C2 = 0x4cf5ad432745937fL;

        public static long[] hash(byte[] key, int offset, int length, long seed) {
            long h1 = seed;
            long h2 = seed;

            // Process blocks of 16 bytes (two 64‑bit words)
            int nblocks = length >>> 4; // length / 16
            ByteBuffer bb = ByteBuffer.wrap(key, offset, length).order(ByteOrder.LITTLE_ENDIAN);

            for (int i = 0; i < nblocks; i++) {
                long k1 = bb.getLong(i * 16);
                long k2 = bb.getLong(i * 16 + 8);

                // mix k1
                k1 *= C1;
                k1 = Long.rotateLeft(k1, 31);
                k1 *= C2;
                h1 ^= k1;
                h1 = Long.rotateLeft(h1, 27);
                h1 += h2;
                h1 = h1 * 5 + 0x52dce729;

                // mix k2
                k2 *= C2;
                k2 = Long.rotateLeft(k2, 33);
                k2 *= C1;
                h2 ^= k2;
                h2 = Long.rotateLeft(h2, 31);
                h2 += h1;
                h2 = h2 * 5 + 0x38495ab5;
            }

            // Tail
            int tailStart = nblocks << 4; // nblocks * 16
            long k1 = 0;
            long k2 = 0;
            int tailLen = length & 15;

            switch (tailLen) {
                case 15: k2 ^= (long) (key[offset + tailStart + 14] & 0xff) << 48;
                case 14: k2 ^= (long) (key[offset + tailStart + 13] & 0xff) << 40;
                case 13: k2 ^= (long) (key[offset + tailStart + 12] & 0xff) << 32;
                case 12: k2 ^= (long) (key[offset + tailStart + 11] & 0xff) << 24;
                case 11: k2 ^= (long) (key[offset + tailStart + 10] & 0xff) << 16;
                case 10: k2 ^= (long) (key[offset + tailStart + 9] & 0xff) << 8;
                case 9:  k2 ^= (long) (key[offset + tailStart + 8] & 0xff);
                         k2 *= C2; k2 = Long.rotateLeft(k2, 33); k2 *= C1; h2 ^= k2;
                case 8:  k1 ^= (long) (key[offset + tailStart + 7] & 0xff) << 56;
                case 7:  k1 ^= (long) (key[offset + tailStart + 6] & 0xff) << 48;
                case 6:  k1 ^= (long) (key[offset + tailStart + 5] & 0xff) << 40;
                case 5:  k1 ^= (long) (key[offset + tailStart + 4] & 0xff) << 32;
                case 4:  k1 ^= (long) (key[offset + tailStart + 3] & 0xff) << 24;
                case 3:  k1 ^= (long) (key[offset + tailStart + 2] & 0xff) << 16;
                case 2:  k1 ^= (long) (key[offset + tailStart + 1] & 0xff) << 8;
                case 1:  k1 ^= (long) (key[offset + tailStart] & 0xff);
                         k1 *= C1; k1 = Long.rotateLeft(k1, 31); k1 *= C2; h1 ^= k1;
                default: // 0 bytes of tail -> no-op
            }

            // Finalization
            h1 ^= length;
            h2 ^= length;

            h1 += h2;
            h2 += h1;

            h1 = fmix64(h1);
            h2 = fmix64(h2);

            h1 += h2;
            h2 += h1;

            return new long[]{h1, h2};
        }

        private static long fmix64(long k) {
            k ^= k >>> 33;
            k *= 0xff51afd7ed558ccdL;
            k ^= k >>> 33;
            k *= 0xc4ceb9fe1a85ec53L;
            k ^= k >>> 33;
            return k;
        }
    }
    // ===============================
    // >>> END OPEN‑SOURCE SNIPPET <<<
    // ===============================
}
