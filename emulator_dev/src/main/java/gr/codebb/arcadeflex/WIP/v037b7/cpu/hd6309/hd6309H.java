/*** hd6309: Portable 6309 emulator ******************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.cpu.hd6309;

public class hd6309H
{
	
/*TODO*///	enum {
/*TODO*///		HD6309_PC=1, HD6309_S, HD6309_CC ,HD6309_A, HD6309_B, HD6309_U, HD6309_X, HD6309_Y, HD6309_DP, HD6309_NMI_STATE,
/*TODO*///		HD6309_IRQ_STATE, HD6309_FIRQ_STATE, HD6309_E, HD6309_F, HD6309_V, HD6309_MD };
/*TODO*///	
	public static final int HD6309_INT_NONE  = 0;	 /* No interrupt required */
	public static final int HD6309_INT_IRQ   = 1;	/* Standard IRQ interrupt */
	public static final int HD6309_INT_FIRQ  = 2;	/* Fast IRQ */
	public static final int HD6309_INT_NMI	 = 4;	/* NMI */	/* NS 970909 */
	public static final int HD6309_IRQ_LINE  = 0;	/* IRQ line number */
	public static final int HD6309_FIRQ_LINE = 1;	 /* FIRQ line number */

/*TODO*///	/* PUBLIC GLOBALS */
/*TODO*///	extern int	hd6309_ICount;
/*TODO*///	
/*TODO*///	
/*TODO*///	/* PUBLIC FUNCTIONS */
/*TODO*///	extern void hd6309_reset(void *param);
/*TODO*///	extern extern int hd6309_execute(int cycles);	/* NS 970908 */
/*TODO*///	extern unsigned hd6309_get_context(void *dst);
/*TODO*///	extern void hd6309_set_context(void *src);
/*TODO*///	extern unsigned hd6309_get_pc(void);
/*TODO*///	extern void hd6309_set_pc(unsigned val);
/*TODO*///	extern unsigned hd6309_get_sp(void);
/*TODO*///	extern void hd6309_set_sp(unsigned val);
/*TODO*///	extern unsigned hd6309_get_reg(int regnum);
/*TODO*///	extern void hd6309_set_reg(int regnum, unsigned val);
/*TODO*///	extern void hd6309_set_nmi_line(int state);
/*TODO*///	extern void hd6309_set_irq_line(int irqline, int state);
/*TODO*///	extern void hd6309_set_irq_callback(int (*callback)(int irqline));
/*TODO*///	extern void hd6309_state_save(void *file);
/*TODO*///	extern void hd6309_state_load(void *file);
/*TODO*///	extern const char *hd6309_info(void *context,int regnum);
/*TODO*///	extern unsigned hd6309_dasm(char *buffer, unsigned pc);
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Read a byte from given memory location									*/
/*TODO*///	/****************************************************************************/
/*TODO*///	/* ASG 971005 -- changed to cpu_readmem16/cpu_writemem16 */
/*TODO*///	#define HD6309_RDMEM(Addr) ((unsigned)cpu_readmem16(Addr))
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Write a byte to given memory location									*/
/*TODO*///	/****************************************************************************/
/*TODO*///	#define HD6309_WRMEM(Addr,Value) (cpu_writemem16(Addr,Value))
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Z80_RDOP() is identical to Z80_RDMEM() except it is used for reading 	*/
/*TODO*///	/* opcodes. In case of system with memory mapped I/O, this function can be	*/
/*TODO*///	/* used to greatly speed up emulation										*/
/*TODO*///	/****************************************************************************/
/*TODO*///	#define HD6309_RDOP(Addr) ((unsigned)cpu_readop(Addr))
/*TODO*///	
/*TODO*///	/****************************************************************************/
/*TODO*///	/* Z80_RDOP_ARG() is identical to Z80_RDOP() except it is used for reading	*/
/*TODO*///	/* opcode arguments. This difference can be used to support systems that	*/
/*TODO*///	/* use different encoding mechanisms for opcodes and opcode arguments		*/
/*TODO*///	/****************************************************************************/
/*TODO*///	#define HD6309_RDOP_ARG(Addr) ((unsigned)cpu_readop_arg(Addr))
/*TODO*///	
/*TODO*///	#ifndef FALSE
/*TODO*///	#	 define FALSE 0
/*TODO*///	#endif
/*TODO*///	#ifndef TRUE
/*TODO*///	#	 define TRUE (!FALSE)
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#ifdef MAME_DEBUG
/*TODO*///	extern unsigned Dasm6309 (char *buffer, unsigned pc);
/*TODO*///	#endif
/*TODO*///	
/*TODO*///	#endif /* _HD6309_H */
	
}
