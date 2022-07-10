package com.satsana.so.services;

import org.springframework.stereotype.Service;

import com.satsana.so.model.Context;
import com.satsana.so.model.GenerationTarget;
import com.satsana.so.model.PolymorphicEngine;
import com.satsana.so.visitors.BashVisitor;
import com.satsana.so.visitors.CSharpVisitor;
import com.satsana.so.visitors.CVisitor;
import com.satsana.so.visitors.JavaScriptVisitor;
import com.satsana.so.visitors.JavaVisitor;
import com.satsana.so.visitors.LanguageVisitor;
import com.satsana.so.visitors.Masm64Visitor;
import com.satsana.so.visitors.PowerShellVisitor;
import com.satsana.so.visitors.PythonVisitor;

@Service
public class ObfuscationServiceImpl implements ObfuscationService {

	@Override
	public String generate(String message, int minOps, int maxOps, GenerationTarget target) {
		PolymorphicEngine engine = new PolymorphicEngine(minOps, maxOps, 16);
		Context context = engine.transform(message);
		LanguageVisitor visitor = getVisitor(target);
		return visitor.visit(context).toString();
	}

	private static LanguageVisitor getVisitor(GenerationTarget target) {
		switch (target) {
			case BASH: 
				return new BashVisitor();
			case C: 
			case CPP: 
				return new CVisitor();
			case C_SHARP: 
				return new CSharpVisitor();
			case JAVA: 
				return new JavaVisitor();
			case JS:
				return new JavaScriptVisitor();
			case MASM64:
				return new Masm64Visitor();
			case POWERSHELL:
				return new PowerShellVisitor();
			case PYTHON:
				return new PythonVisitor();
			default:
				throw new RuntimeException("No visitor bound to this target");
		}
	}
}
