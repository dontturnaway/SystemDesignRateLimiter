package com.test.ratelimiter.model;

public class FilterFieldIP extends FilterField<byte[]>{

    public FilterFieldIP(String fieldValue) {
        super(ipv4StringToBytes(fieldValue));
    }


    public static byte[] ipv4StringToBytes(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ip);
        }

        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            int octet = Integer.parseInt(parts[i]);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException("Invalid IPv4 octet: " + parts[i]);
            }
            bytes[i] = (byte) octet;
        }
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterFieldIP other = (FilterFieldIP) o;
        return java.util.Arrays.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(this.value);
    }
}
