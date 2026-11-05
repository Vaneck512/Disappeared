package ru.vaneck.disappeared.files;

import java.util.Objects;

public class SoundInstanceRecord {
    public String path;
    public String fileName;
    public float volume;
    public boolean active;

    public SoundInstanceRecord(String path, String fileName, float volume, boolean active) {
        this.path = path;
        this.fileName = fileName;
        this.volume = volume;
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        return this.fileName.equals(((SoundInstanceRecord) obj).fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fileName);
    }

    @Override
    public String toString() {
        return "SoundInstanceRecord{" +
                "path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                ", volume=" + volume +
                ", active=" + active +
                '}';
    }
}
