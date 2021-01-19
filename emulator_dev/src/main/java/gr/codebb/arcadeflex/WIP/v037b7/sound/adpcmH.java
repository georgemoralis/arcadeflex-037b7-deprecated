/*
 * ported to v0.37b7 
 * ported to v0.36 
*/
package gr.codebb.arcadeflex.WIP.v037b7.sound;

public class adpcmH {

    public static final int MAX_ADPCM = 8;

    public static class ADPCMinterface {

        public ADPCMinterface(int num, int frequency, int region, int[] mixing_level) {
            this.num = num;
            this.frequency = frequency;
            this.region = region;
            this.mixing_level = mixing_level;
        }

        public int num;/* total number of ADPCM decoders in the machine */
        public int frequency;/* playback frequency */
        public int region;/* memory region where the samples come from */
        public int[] mixing_level;/* master volume */
    }
}
