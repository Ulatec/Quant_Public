package BackTest;

import java.util.Objects;

public class VolumeKey {
    private int hour;

    private int minute;

    public VolumeKey(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VolumeKey volumeKey = (VolumeKey) o;
        return hour == volumeKey.hour && minute == volumeKey.minute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute);
    }
}
