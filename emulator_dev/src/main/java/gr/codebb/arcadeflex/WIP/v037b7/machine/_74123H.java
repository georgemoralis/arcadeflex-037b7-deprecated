/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.machine;

public class _74123H {

    public static final int MAX_TTL74123 = 4;

    public static abstract interface output_changed_cbPtr {

        public abstract void handler();
    }

    /* The interface structure */
    public static class TTL74123_interface {

        public TTL74123_interface(double res, double cap, output_changed_cbPtr output_changed_cb) {
            this.res = res;
            this.cap = cap;
            this.output_changed_cb = output_changed_cb;
        }
        double res;
        double cap;
        output_changed_cbPtr output_changed_cb;
    }
}
