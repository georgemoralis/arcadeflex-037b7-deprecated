/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

public class _8255ppiH {

    public static final int MAX_8255 = 4;

    public static abstract interface PortReadHandlerPtr {

        public abstract int handler(int chip);
    }

    public static abstract interface PortWriteHandlerPtr {

        public abstract void handler(int chip, int data);
    }

    public static class ppi8255_interface {

        public ppi8255_interface(int num, PortReadHandlerPtr portA_r, PortReadHandlerPtr portB_r, PortReadHandlerPtr portC_r, PortWriteHandlerPtr portA_w, PortWriteHandlerPtr portB_w, PortWriteHandlerPtr portC_w) {
            this.num = num;
            this.portA_r = portA_r;
            this.portB_r = portB_r;
            this.portC_r = portC_r;
            this.portA_w = portA_w;
            this.portB_w = portB_w;
            this.portC_w = portC_w;
        }
        int num;
        /* number of PPIs to emulate */
        PortReadHandlerPtr portA_r;
        PortReadHandlerPtr portB_r;
        PortReadHandlerPtr portC_r;
        PortWriteHandlerPtr portA_w;
        PortWriteHandlerPtr portB_w;
        PortWriteHandlerPtr portC_w;
    }

}
