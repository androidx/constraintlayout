package androidx.constraintlayout.extension.util;

import java.util.HashMap;

public class Vp {
    static HashMap<String, MSChannel> channels = new HashMap<>();

    static class MSChannel {
        int mSize = 32000;
        String mName;
        int mLast = 0;
        int mWritten = 0;
        long[] mTime = new long[mSize];

        private MSChannel() {

        }

        private MSChannel(int size) {
            mSize = size;
            mTime = new long[mSize];
        }

        int getLatest() {
            if (mWritten == 0) {
                return -1;
            }
            return (mLast == 0 ? mSize : mLast) - 1;
        }

        long getPos() {
            int written;
            int last;
            synchronized (this) {
                written = mWritten;
                last = mLast;
            }
            return ((long) written) << 32 | last;
        }

        void increment() {
            synchronized (this) {
                mLast++;
                if (mLast == mSize) {
                    mLast = 0;
                }
                mWritten++;
            }
        }

        synchronized int getLast() {
            return mLast;
        }
    }

    // ======================== Support for float ==============================
    static class MSChannelFloat extends MSChannel {
        float[] mData = new float[mSize];

        MSChannelFloat(String name) {
            this.mName = name;
        }

        MSChannelFloat(String name, int size) {
            super(size);
            this.mName = name;
            mData = new float[size];
        }

        void add(long time, float value) {
           int  last = getLast();
            mTime[last] = time;
            mData[last] = value;
            increment();
        }


        public int get(long start, long[] time, float[] value) {
            int count = 0;

            int written;
            int last, first;
            long firstTime;
            long lastTime;
            synchronized (this) {
                written = mWritten;
                last = mLast;

                if (written > mSize) {
                    firstTime = mTime[(last + 1) % mSize];
                } else {
                    firstTime = mTime[last - written];
                }
                int lt = last - 1;
                lastTime = mTime[lt >= 0 ? lt : lt + mSize];
            }
            int startIndex = -1;
            if (mWritten > mSize && mLast != mSize - 1) { // it has wrapped
                if (mTime[mSize - 1] > start) {
                    startIndex = search(mTime, last, mSize - 1, start);
                } else {
                    startIndex = search(mTime, 0, last, start);
                }
            } else {
                startIndex = search(mTime, 0, last, start);
            }
            return getIndex(startIndex, time, value);
        }

        static int search(long arr[], int low, int high, long key) {
            int pos = -1;
            while (low <= high) {
                pos = low + (high - low) / 2;
                if (arr[pos] == key)
                    return pos;

                if (arr[pos] < key)
                    low = pos + 1;
                else
                    high = pos - 1;
            }
            return pos;
        }

        public void findIndex(long[] time, int start, int end, int key) {

        }


        public int getIndex(int startIndex, long[] time, float[] value) {
            int len = time.length;
            if (len + startIndex < mSize) {
                System.arraycopy(mTime, startIndex, time, 0, len);
                System.arraycopy(mData, startIndex, value, 0, len);
                return (len + startIndex > mLast) ? mLast - startIndex : len;
            } else {
                int block1len = mSize - startIndex;
                System.arraycopy(mTime, startIndex, time, 0, block1len);
                System.arraycopy(mData, startIndex, value, 0, block1len);
                int copyLen = (mLast > len - block1len) ? len - block1len : mLast;
                System.arraycopy(mTime, 0, time, block1len, copyLen);
                System.arraycopy(mData, 0, value, block1len, copyLen);
                return block1len + copyLen;
            }

        }

        int getLatest(long[] time, float[] value) {
            int written;
            int last;
            synchronized (this) {
                written = mWritten;
                last = mLast;
            }
            int maxLen = time.length;
            if (written == 0) return -1;
            if (maxLen > written) {
                maxLen = (int) mWritten;
            }
            if (maxLen > mSize) {
                maxLen = mSize;
            }
            if (last - maxLen < 0) {   // copy [0....last, size-(maxLen-last)...size-1]
                int copy1src = 0;
                int copy1Dest = maxLen - last;
                int copy1len = last;

                System.arraycopy(mTime, copy1src, time, copy1Dest, copy1len);
                System.arraycopy(mData, copy1src, value, copy1Dest, copy1len);
                int copy2src = mSize - (maxLen - last);
                int copy2Dest = 0;
                int copy2len = maxLen - last;

                System.arraycopy(mTime, copy2src, time, copy2Dest, copy2len);
                System.arraycopy(mData, copy2src, value, copy2Dest, copy2len);
            } else {
                int copy1src = last - maxLen;
                int copy1Dest = 0;
                int copy1len = maxLen;
                System.arraycopy(mTime, copy1src, time, copy1Dest, copy1len);
                System.arraycopy(mData, copy1src, value, copy1Dest, copy1len);
            }

            return maxLen;
        }
    }

    public static void initChannel(String channel, int size) {
        channels.put(channel, new MSChannelFloat(channel, size));
    }


    public static void send(String channel, float value) {
        send(channel, System.nanoTime(), value);
    }

    public static void send(String channel, long time, float value) {
        MSChannelFloat c = (MSChannelFloat) channels.get(channel);
        if (c == null) {
            c = new MSChannelFloat(channel);
            channels.put(channel, c);
        }
        c.add(time, value);
    }

    public static int getLatest(String channel, long[] times, float[] values) {
        MSChannelFloat c = (MSChannelFloat) channels.get(channel);
        if (c == null) {
            return -1;
        }
        return c.getLatest(times, values);
    }

    public static int totalWritten(String channel) {
        MSChannelFloat c = (MSChannelFloat) channels.get(channel);
        if (c == null) {
            return -1;
        }
        return c.mWritten;
    }

    public static int getAfter(String channel, long time, long[] times, float[] values) {
        MSChannelFloat c = (MSChannelFloat) channels.get(channel);
        if (c == null) {
            return -1;
        }
        return c.get(time, times, values);
    }

    public static void sinWaveGen(String channel) {
        System.out.println("================ sinWaveGen =================");
        Thread t = new Thread() {
            @Override
            public void run() {

                long[] time = new long[100];
                float[] value = new float[100];
                for (int i = 0; i < 100000000; i++) {
                    long nt = System.nanoTime();
                    float v = (float) (Math.sin(nt * 1E-9 * Math.PI) * Math.sin(nt * 1E-9 * Math.PI / 40));
                    Vp.send(channel, nt, v);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.setDaemon(false);
        t.start();
    }

    private static final HashMap<String, long[]> last_fps_time = new HashMap<>();

    public static void fps(String channel) {
        long[] ans = last_fps_time.get(channel);
        if (ans == null) {
            ans = new long[1];
            last_fps_time.put(channel, ans);
            ans[0] = System.nanoTime();
        }
        long now = System.nanoTime();
        float duration = (now - ans[0]) * 1E-9f;

        if (duration < 1 / 500f) {
            send(channel, now, 0);
        } else {
            send(channel, now, 1 / duration);
        }
        ans[0] = now;

    }

    // ======================= END Support for Floats==================================

    public static void main(String[] args) throws InterruptedException {
        long last = System.nanoTime();
        Thread t = new Thread() {
            @Override
            public void run() {

                long[] time = new long[100];
                float[] value = new float[100];
                for (int i = 0; i < 100000000; i++) {
                    int len = getLatest("bob", time, value);
                    if (len == -1) {
                        System.out.println("skip");
                        try {
                            Thread.sleep(0, 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    long current = System.nanoTime();
                    System.out.println("                       " + len + " " + (current - time[len - 1]) * 1E-6f + "ms " + value[len - 1]);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.setDaemon(false);
        t.start();
        int sample = 10000000;
        for (long i = 0; i < 100000000000L; i++) {
            send("bob", i);
            //  Thread.sleep(0,100);
            if (i % sample == 0) {
                long now = System.nanoTime();

                System.out.println(i + " per sec =" + sample / ((now - last) * 1E-9));
                last = now;
            }

        }

    }
}
