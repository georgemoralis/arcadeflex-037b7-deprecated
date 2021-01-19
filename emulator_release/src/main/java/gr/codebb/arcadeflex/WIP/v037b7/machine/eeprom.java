/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.machine;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.strlen;
import static gr.codebb.arcadeflex.WIP.v037b7.machine.eepromH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.fileio.*;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.memcpy;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.strncmp;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.*;
import static gr.codebb.arcadeflex.old.mame.usrintrf.*;

public class eeprom {

    public static int SERIAL_BUFFER_LENGTH = 40;
    public static int MEMORY_SIZE = 1024;

    static EEPROM_interface intf;

    static int serial_count;
    static char[] serial_buffer = new char[SERIAL_BUFFER_LENGTH];
    static /*unsigned*/ char[] eeprom_data = new char[MEMORY_SIZE];
    static int eeprom_data_bits;
    static int eeprom_read_address;
    static int eeprom_clock_count;
    static int latch, reset_line, clock_line, sending;
    static int locked;
    static int reset_delay;

    public static void EEPROM_init(EEPROM_interface _interface) {
        intf = _interface;

        if ((1 << intf.address_bits) * intf.data_bits / 8 > MEMORY_SIZE) {
            usrintf_showmessage("EEPROM larger than eeprom.c allows");
            return;
        }
        for (int i = 0; i < (1 << intf.address_bits) * intf.data_bits / 8; i++) {
            eeprom_data[i] = 0xff; //memset(eeprom_data,0xff,(1 << intf.address_bits) * intf.data_bits / 8);
        }
        serial_count = 0;
        latch = 0;
        reset_line = ASSERT_LINE;
        clock_line = ASSERT_LINE;
        sending = 0;
        if (intf.cmd_unlock != null) {
            locked = 1;
        } else {
            locked = 0;
        }
    }

    static void EEPROM_write(int bit) {
        logerror("EEPROM write bit %d\n", bit);

        if (serial_count >= SERIAL_BUFFER_LENGTH - 1) {
            logerror("error: EEPROM serial buffer overflow\n");
            return;
        }
        serial_buffer[serial_count++] = (bit != 0 ? '1' : '0');
        serial_buffer[serial_count] = '\0';/* nul terminate so we can treat it as a string */
        if ((intf.cmd_read != null)
                && (serial_count == (strlen(intf.cmd_read) + intf.address_bits))
                && !strncmp(serial_buffer, intf.cmd_read, strlen(intf.cmd_read))) {
            int i, address;

            address = 0;
            for (i = 0; i < intf.address_bits; i++) {
                address <<= 1;
                if (serial_buffer[i + strlen(intf.cmd_read)] == '1') {
                    address |= 1;
                }
            }
            if (intf.data_bits == 16) {
                eeprom_data_bits = (eeprom_data[2 * address + 0] << 8) + eeprom_data[2 * address + 1];
            } else {
                eeprom_data_bits = eeprom_data[address];
            }
            eeprom_read_address = address;
            eeprom_clock_count = 0;
            sending = 1;
            serial_count = 0;
            logerror("EEPROM read %04x from address %02x\n", eeprom_data_bits, address);
        } else if (intf.cmd_erase != null && serial_count == (strlen(intf.cmd_erase) + intf.address_bits)
                && !strncmp(serial_buffer, intf.cmd_erase, strlen(intf.cmd_erase))) {
            int i, address;

            address = 0;
            for (i = 0; i < intf.address_bits; i++) {
                address <<= 1;
                if (serial_buffer[i + strlen(intf.cmd_erase)] == '1') {
                    address |= 1;
                }
            }
            logerror("EEPROM erase address %02x\n", address);
            if (locked == 0) {
                if (intf.data_bits == 16) {
                    eeprom_data[2 * address + 0] = 0x00;
                    eeprom_data[2 * address + 1] = 0x00;
                } else {
                    eeprom_data[address] = 0x00;
                }
            } else {
                logerror("Error: EEPROM is locked\n");
            }
            serial_count = 0;
        } else if (intf.cmd_write != null && serial_count == (strlen(intf.cmd_write) + intf.address_bits + intf.data_bits)
                && !strncmp(serial_buffer, intf.cmd_write, strlen(intf.cmd_write))) {
            int i, address, data;

            address = 0;
            for (i = 0; i < intf.address_bits; i++) {
                address <<= 1;
                if (serial_buffer[i + strlen(intf.cmd_write)] == '1') {
                    address |= 1;
                }
            }
            data = 0;
            for (i = 0; i < intf.data_bits; i++) {
                data <<= 1;
                if (serial_buffer[i + strlen(intf.cmd_write) + intf.address_bits] == '1') {
                    data |= 1;
                }
            }
            logerror("EEPROM write %04x to address %02x\n", data, address);
            if (locked == 0) {
                if (intf.data_bits == 16) {
                    eeprom_data[2 * address + 0] = (char) ((data >> 8) & 0xff);
                    eeprom_data[2 * address + 1] = (char) (data & 0xff);
                } else {
                    eeprom_data[address] = (char) (data & 0xff);
                }
            } else {
                logerror("Error: EEPROM is locked\n");
            }
            serial_count = 0;
        } else if (intf.cmd_lock != null && serial_count == strlen(intf.cmd_lock)
                && !strncmp(serial_buffer, intf.cmd_lock, strlen(intf.cmd_lock))) {
            logerror("EEPROM lock\n");
            locked = 1;
            serial_count = 0;
        } else if (intf.cmd_unlock != null && serial_count == strlen(intf.cmd_unlock)
                && !strncmp(serial_buffer, intf.cmd_unlock, strlen(intf.cmd_unlock))) {
            logerror("EEPROM unlock\n");
            locked = 0;
            serial_count = 0;
        }
    }

    static void EEPROM_reset() {
        if (serial_count != 0) {
            logerror("EEPROM reset, buffer = %s\n", serial_buffer);
        }

        serial_count = 0;
        sending = 0;
        reset_delay = 5;/* delay a little before returning setting data to 1 (needed by wbeachvl) */
    }

    public static void EEPROM_write_bit(int bit) {
        logerror("write bit %d\n", bit);
        latch = bit;
    }

    public static int EEPROM_read_bit() {
        int res;

        if (sending != 0) {
            res = (eeprom_data_bits >> intf.data_bits) & 1;
        } else {
            if (reset_delay > 0) {
                /* this is needed by wbeachvl */
                reset_delay--;
                res = 0;
            } else {
                res = 1;
            }
        }

        logerror("read bit %d\n", res);
        return res;
    }

    public static void EEPROM_set_cs_line(int state) {
        logerror("set reset line %d\n", state);
        reset_line = state;

        if (reset_line != CLEAR_LINE) {
            EEPROM_reset();
        }
    }

    public static void EEPROM_set_clock_line(int state) {
        logerror("set clock line %d\n", state);
        if (state == PULSE_LINE || (clock_line == CLEAR_LINE && state != CLEAR_LINE)) {
            if (reset_line == CLEAR_LINE) {
                if (sending != 0) {
                    if (eeprom_clock_count == intf.data_bits && intf.enable_multi_read != 0) {
                        eeprom_read_address = (eeprom_read_address + 1) & ((1 << intf.address_bits) - 1);
                        if (intf.data_bits == 16) {
                            eeprom_data_bits = (eeprom_data[2 * eeprom_read_address + 0] << 8) + eeprom_data[2 * eeprom_read_address + 1];
                        } else {
                            eeprom_data_bits = eeprom_data[eeprom_read_address];
                        }
                        eeprom_clock_count = 0;
                        logerror("EEPROM read %04x from address %02x\n", eeprom_data_bits, eeprom_read_address);
                    }
                    eeprom_data_bits = (eeprom_data_bits << 1) | 1;
                    eeprom_clock_count++;
                } else {
                    EEPROM_write(latch);
                }
            }
        }

        clock_line = state;
    }

    public static void EEPROM_load(Object f) {
        osd_fread(f, eeprom_data, 0, (1 << intf.address_bits) * intf.data_bits / 8);
    }

    public static void EEPROM_save(Object f) {
        osd_fwrite(f, eeprom_data, 0, (1 << intf.address_bits) * intf.data_bits / 8);
    }

    public static void EEPROM_set_data(UBytePtr data, int length) {
        memcpy(eeprom_data, data, length);
    }
}
