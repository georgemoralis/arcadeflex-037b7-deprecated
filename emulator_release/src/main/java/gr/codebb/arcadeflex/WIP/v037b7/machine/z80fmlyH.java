package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class z80fmlyH {

    public static final int MAX_CTC = 2;

    public static final int NOTIMER_0 = (1 << 0);
    public static final int NOTIMER_1 = (1 << 1);
    public static final int NOTIMER_2 = (1 << 2);
    public static final int NOTIMER_3 = (1 << 3);

    public static abstract interface IntrPtr {

        public abstract void handler(int which);
    }

    public static abstract interface PortCallbackPtr {

        public abstract void handler(int data);
    }

    public static class z80ctc_interface {

        public z80ctc_interface(int num, int[] baseclock, int[] notimer, IntrPtr[] intr, WriteHandlerPtr zc0[], WriteHandlerPtr zc1[], WriteHandlerPtr zc2[]) {
            this.num = num;
            this.baseclock = baseclock;
            this.notimer = notimer;
            this.intr = intr;
            this.zc0 = zc0;
            this.zc1 = zc1;
            this.zc2 = zc2;
        }
        public int num;/* number of CTCs to emulate */
        public int[] baseclock;//[MAX_CTC];                           /* timer clock */
        public int[] notimer;//[MAX_CTC];                         /* timer disablers */
        public IntrPtr[] intr;//void (*intr[MAX_CTC])(int which);             /* callback when change interrupt status */
        public WriteHandlerPtr zc0[];//[MAX_CTC];   /* ZC/TO0 callback */
        public WriteHandlerPtr zc1[];//[MAX_CTC];   /* ZC/TO1 callback */
        public WriteHandlerPtr zc2[];//[MAX_CTC];   /* ZC/TO2 callback */
    }


    /*--------------------------------------------------------------------*/
    public static final int MAX_PIO = 1;

    public static class z80pio_interface {

        public z80pio_interface(int num, IntrPtr[] intr, PortCallbackPtr[] rdyA, PortCallbackPtr[] rdyB) {
            this.num = num;
            this.intr = intr;
            this.rdyA = rdyA;
            this.rdyB = rdyB;
        }

        public int num;/* number of PIOs to emulate */
        public IntrPtr[] intr;//void (*intr[MAX_CTC])(int which);             /* callback when change interrupt status */
        public PortCallbackPtr[] rdyA;//[MAX_PIO])            /* portA ready active callback (do not support yet)*/
        public PortCallbackPtr[] rdyB;//[MAX_PIO])         /* portB ready active callback (do not support yet)*/

    }
}
