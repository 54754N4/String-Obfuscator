package com.satsana.so.visitors;

import com.satsana.so.model.Context;
import com.satsana.so.transforms.Add;
import com.satsana.so.transforms.MulMod;
import com.satsana.so.transforms.MulModInv;
import com.satsana.so.transforms.Not;
import com.satsana.so.transforms.Permutation;
import com.satsana.so.transforms.RotateLeft;
import com.satsana.so.transforms.RotateRight;
import com.satsana.so.transforms.Substract;
import com.satsana.so.transforms.Xor;

public class Masm64Visitor extends LanguageVisitor {
	public static final int[] IMMEDIATE_SIZES = { 2, 4, 8, 16 };
	public static final String[] DATA_TYPES = { "db", "dw", "dd", "dq" };
	public static final String[] DATA_TYPES_PTR = { "byte", "word", "dword", "qword" };
	public static final String[][] REGISTERS = {
			{ "al", "ax", "eax", "rax" },
			{ "bl", "bx", "ebx", "rbx" },
			{ "cl", "cx", "ecx", "rcx" },
			{ "dl", "dx", "edx", "rdx" },
			{ "dil", "di", "edi", "rdi" },
			{ "sil", "si", "esi", "rsi" },
			{ "bpl", "bp", "ebp", "rbp" },
			{ "spl", "sp", "esp", "rsp" },
			{ "r8l", "r8w", "r8d", "r8" },
			{ "r9l", "r9w", "r9d", "r9" },
			{ "r10l", "r10w", "r10d", "r10" },
			{ "r11l", "r11w", "r11d", "r11" },
			{ "r12l", "r12w", "r12d", "r12" },
			{ "r13l", "r13w", "r13d", "r13" },
			{ "r14l", "r14w", "r14d", "r14" },
			{ "r15l", "r15w", "r15d", "r15" },
	};
	public static final int RAX = 0, RBX = 1, RCX = 2, RDX = 3,
			RDI = 4, RSI = 5, RBP = 6, RSP = 7, R8 = 8, R9 = 9,
			R10 = 10, R11 = 11, R12 = 12, R13 = 13, R14 = 14, R15 = 15;
	
	private int block, size, shadowSpace, increment;
	private String result, loopName, i, variable;
	
	/* Convenience methods */
	
	private String reg(int id) {
		return REGISTERS[id][block];
	}
	
	@Override
	public String hex(long l) {
		return String.format("0%0"+IMMEDIATE_SIZES[block]+"xh", l);
	}

	/* RBX = data array address
	 * RCX = i loop counter
	 * RDX = data variable for transformations
	 */
	@Override
	public StringBuilder initialise(Context ctx) {
		// Calculate correct data sizes and registers
		block = (ctx.getMaxBits()-1)/8;
		increment = IMMEDIATE_SIZES[block]/2;
		result = "string";
		loopName = generateName();
		shadowSpace = 32;
		i = reg(RCX);
		variable = reg(RDX);
		StringBuilder sb = new StringBuilder();
		// Write imports
		sb.append("extern GetStdHandle: proc\n"
				+ "extern WriteFile: proc\n"
				+ "extern GetFileType: proc\n"
				+ "extern WriteConsoleW: proc\n\n");
		// Write bytes in data section
		sb.append(".data?\n"
				+ "\tstdout\tdq ?\n"
				+ "\twritten\tdq ?\n");
		sb.append(".data\n")
			.append("\t"+result+" "+DATA_TYPES[block]+" ");
		long[] bytes = ctx.getBytes();
		size = bytes.length;
		for (long b : bytes)
			sb.append(hex(b)).append(",");
		sb.deleteCharAt(sb.length()-1)
			.append("\n")
			.append("\tlen\tequ $-"+result+"\n");
		// Write code section and prolog
		sb.append(".code\n")
			.append("main proc\n")
			.append(String.format("\tpush\trbp\n"))
			.append(String.format("\tmov\trbp, rsp\n"))
			.append(String.format("\tsub\trsp, %d\n", shadowSpace))
			.append(String.format("\tand\trsp, -10h\n\n"));
		// Loop initializing
		sb.append(String.format("\tmov\trbx, offset %s\n", result))
			.append(String.format("\txor\trcx, rcx\n"))
			.append(loopName+":\n")
			.append("\txor\trax, rax\n"
					+ "\txor\trdx, rdx\n"
					+ "\txor\tr8, r8\n"
					+ "\txor\tr9, r9\n"
					+ "\txor\tr10, r10\n")
			.append(String.format("\tmov\t%s, %s ptr [rbx + rcx*%d]\n", variable, DATA_TYPES_PTR[block], increment));
		return sb;
	}

	@Override
	public void finalise(StringBuilder in) {
		// End of loop
		in.append(String.format("\tmov\t%s ptr [rbx + rcx*%d], %s\n", DATA_TYPES_PTR[block], increment, variable))
			.append(String.format("\tinc\t%s\n", i))
			.append(String.format("\tcmp\t%s, %s\n", i, size))
			.append(String.format("\tjne\t%s\n\n", loopName));
		// Printing output
		in.append("\t; Printing code\n"
				+ "\txor\trax, rax\n"
				+ "\txor\trcx, rcx\n"
				+ "\txor\trdx, rdx\n"
				+ "\txor\tr8, r8\n"
				+ "\txor\tr9, r9\n"
				+ "\tmov\trcx, -11\n"
				+ "\tcall\tGetStdHandle\n"
				+ "\tmov\t[stdout], rax\n"
				+ "\tmov\trcx, rax\n"
				+ "\tcall\tGetFileType\n"
				+ "\tcmp\trax, 1\n"
				+ "\tje\tfileWrite\n"
				+ "\tmov\trcx, [stdout]\n"
				+ "\tmov\trdx, rbx\n"
				+ "\tmov\tr8, len\n"
				+ "\tmov\tr9, written\n"
				+ "\tcall\tWriteConsoleW\n"
				+ "\tjmp\tepilog\n"
				+ "fileWrite:\n"
				+ "\tmov\trcx, [stdout]\n"
				+ "\tmov\trdx, rbx\n"
				+ "\tmov\tr8, len\n"
				+ "\tmov\tr9, written\n"
				+ "\tcall\tWriteFile\n"
				+ "epilog:\n"
				+ "\tadd\trsp, "+shadowSpace+"\n"
				+ "\tmov\trsp, rbp\n"
				+ "\tpop\trbp\n"
				+ "\tret\n"
				+ "main endp\n"
				+ "end");
	}

	@Override
	public void visit(Add a, StringBuilder in) {
		in.append(String.format("\tadd\t%s, %d\n", variable, a.getValue()));
	}

	@Override
	public void visit(MulMod mm, StringBuilder in) {	// https://stackoverflow.com/a/8022107/3225638
		String rax = reg(RAX), rdx = reg(RDX), 
				r8 = reg(R8);
		// Multiplication
		in.append(String.format("\tmov\t%s, %s\n", rax, rdx))
			.append(String.format("\txor\t%s, %s\n", rdx, rdx))
			.append(String.format("\tmov\t%s, %d\n", r8, mm.getValue()))
			.append(String.format("\tmul\t%s\n", r8))
			.append(String.format("\tmov\t%s, %s\n", rdx, rax));
		// Remainder
		in.append(String.format("\tmov\t%s, %s\n", rax, rdx))
			.append(String.format("\txor\t%s, %s\n", rdx, rdx))
			.append(String.format("\tmov\t%s, %d\n", r8, mm.getModulo()))
			.append(String.format("\tdiv\t%s\n", r8))
			.append(String.format("\tmov\t%s, %s\n", rdx, rax));
	}

	@Override
	public void visit(MulModInv mmi, StringBuilder in) {
		visit(MulMod.class.cast(mmi), in);
	}

	@Override
	public void visit(Not n, StringBuilder in) {
		in.append(String.format("\tnot\t%s\n", variable));
	}

	@Override
	public void visit(Permutation p, StringBuilder in) {
		String r8 = reg(R8), r9 = reg(R9), r10 = reg(R10);
		in.append(String.format("\tmov\t%s, %s\n", r8, variable))
			.append(String.format("\tshr\t%s, %d\n", r8, p.getPosition1()))
			.append(String.format("\tmov\t%s, %s\n", r9, variable))
			.append(String.format("\tshr\t%s, %d\n", r9, p.getPosition2()))
			.append(String.format("\txor\t%s, %s\n", r8, r9))
			.append(String.format("\tmov\t%s, %d\n", r9, 1))
			.append(String.format("\tshl\t%s, %d\n", r9, p.getBits()))
			.append(String.format("\tsub\t%s, %d\n", r9, 1))
			.append(String.format("\tand\t%s, %s\n", r8, r9))
			.append(String.format("\tmov\t%s, %s\n", r9, r8))
			.append(String.format("\tshl\t%s, %d\n", r9, p.getPosition1()))
			.append(String.format("\tmov\t%s, %s\n", r10, r8))
			.append(String.format("\tshl\t%s, %d\n", r10, p.getPosition2()))
			.append(String.format("\tor\t%s, %s\n", r9, r10))
			.append(String.format("\txor\t%s, %s\n", variable, r9));
	}

	@Override
	public void visit(RotateLeft rl, StringBuilder in) {
		in.append(String.format("\trol\t%s, %d\n", variable, rl.getValue()));
	}

	@Override
	public void visit(RotateRight rr, StringBuilder in) {
		in.append(String.format("\tror\t%s, %d\n", variable, rr.getValue()));
	}

	@Override
	public void visit(Substract s, StringBuilder in) {
		in.append(String.format("\tsub\t%s, %d\n", variable, s.getValue()));
	}

	@Override
	public void visit(Xor x, StringBuilder in) {
		in.append(String.format("\txor\t%s, %d\n", variable, x.getValue()));
	}
}
