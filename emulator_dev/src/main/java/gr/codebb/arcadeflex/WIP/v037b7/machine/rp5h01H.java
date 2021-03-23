/*
 * ported to v0.37b7
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

public class rp5h01H {

    /* max simultaneous chips supported. change if you need more */
    public static final int MAX_RP5H01 = 1;

    public static class RP5H01_interface {

        public RP5H01_interface(int num, int[] region, int[] offset) {
            this.num = num;
            this.region = region;
            this.offset = offset;
        }

        int num;
        /* number of chips */
        int[] region;//[MAX_RP5H01];		/* memory region where data resides */
        int[] offset;//[MAX_RP5H01];		/* memory offset within the above region where data resides */
    }

}
