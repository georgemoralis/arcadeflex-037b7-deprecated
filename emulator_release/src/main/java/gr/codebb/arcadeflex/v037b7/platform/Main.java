/*
 * ported to 0.37b7 
 */
package gr.codebb.arcadeflex.v037b7.platform;

import static gr.codebb.arcadeflex.old2.arcadeflex.libc_v2.*;

import gr.codebb.arcadeflex.old.arcadeflex.osdepend;

public class Main {

    public static void main(String[] args) {
        ConvertArguments("arcadeflex", args);
        System.exit(osdepend.main(argc, argv));
    }
}
