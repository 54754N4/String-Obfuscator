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

public class PowerShellVisitor extends LanguageVisitor {
	private String variable, array, temp, i, result, mask;
	private boolean hasPermutation;
	
	@Override
	public StringBuilder initialise(Context context) {
		// Generate variable names
		variable = "$"+generateName();
		temp = "$"+generateName();
		i = "$"+generateName();
		array = "$"+generateName();
		result = "$string";
		mask = hex(context.getMask());
		hasPermutation = context.getReverse().containsPermutation();
		// Write bytes in string
		StringBuilder sb = new StringBuilder();
		sb.append("[uint64[]]"+array+" = ");
		for (long b : context.getBytes())
			sb.append(hex(b)+",");
		sb.deleteCharAt(sb.length()-1) 	// remove last comma
			.append("\n");
		sb.append(result+" = [System.Text.StringBuilder]::new()\n");
		// Write for loop
		sb.append(String.format("for (%s = 0; %s -lt %s.Length; %s++) {\n", i, i, array, i));
		sb.append("\t"+variable+" = "+array+"["+i+"]\n");
		return sb;
	}

	@Override
	public void finalise(StringBuilder in) {
		String deleteFormat = "%1$s = [void]%1$s\n";
		in.append(String.format("\t[void]%s.Append([char](%s -band %s))\n", result, variable, mask))
			.append("}\n")
			.append(String.format(deleteFormat, variable))
			.append(String.format(deleteFormat, i))
			.append(String.format(deleteFormat, array));
		if (hasPermutation)
			in.append(String.format(deleteFormat, temp));
		in.append("Write-Host "+result+".ToString()");
	}

	@Override
	public void visit(Add a, StringBuilder in) {
		if (a.getValue() == 1) {
			in.append("\t").append(variable)
				.append("++\n");
			return;
		}
		in.append("\t").append(variable)
			.append(" += ").append(hex(a.getValue()))
			.append("\n");
	}

	@Override
	public void visit(MulMod mm, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" = ")
			.append("("+variable).append(" * "+hex(mm.getValue())+") % "+hex(mm.getModulo()))
			.append("\n");
	}

	@Override
	public void visit(MulModInv mmi, StringBuilder in) {
		visit(MulMod.class.cast(mmi), in);
	}

	@Override
	public void visit(Not n, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" = ")
			.append("-bnot "+variable+" -band "+hex(n.getMask()))
			.append("\n");
	}

	@Override
	public void visit(Permutation p, StringBuilder in) {
		in.append("\t").append(temp)
			.append(" = ").append("(("+variable+" -shr "+hex(p.getPosition1())+")")
			.append(" -bxor ("+variable+" -shr "+hex(p.getPosition2())+")) -band ((1 -shl "+hex(p.getBits())+") - 1)")
			.append("\n");
		in.append("\t").append(variable)
			.append(" = ").append(variable+" -bxor (").append("("+temp+" -shl "+hex(p.getPosition1())+") -bor ("+temp+" -shl "+hex(p.getPosition2())+"))")
			.append("\n");
	}

	@Override
	public void visit(RotateLeft rl, StringBuilder in) {
		String mask = hex(rl.getMask());
		in.append("\t").append(variable)
			.append(" = ").append("((("+variable+" -band "+mask+") -shr "+hex(rl.lhs())+") -bor (")
			.append(variable+" -shl "+hex(rl.rhs())+")) -band "+mask)
			.append("\n");
	}

	@Override
	public void visit(RotateRight rr, StringBuilder in) {
		String mask = hex(rr.getMask());
		in.append("\t").append(variable)
			.append(" = ").append("((("+variable+" -band "+mask+") -shl "+hex(rr.lhs())+") -bor (")
			.append(variable+" -shr "+hex(rr.rhs())+")) -band "+mask)
			.append("\n");
	}

	@Override
	public void visit(Substract s, StringBuilder in) {
		if (s.getValue() == 1) {
			in.append("\t").append(variable)
				.append("--\n");
			return;
		}
		in.append("\t").append(variable)
			.append(" -= ").append(hex(s.getValue()))
			.append("\n");
	}

	@Override
	public void visit(Xor x, StringBuilder in) {
		in.append("\t").append(variable)
			.append(" = "+variable+" -bxor ").append(hex(x.getValue()))
			.append("\n");
	}
}
