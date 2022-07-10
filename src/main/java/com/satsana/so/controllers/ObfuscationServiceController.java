package com.satsana.so.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.satsana.so.model.GenerationTarget;
import com.satsana.so.services.ObfuscationService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/obfuscate")
public class ObfuscationServiceController {
	@Autowired
	private ObfuscationService service;
	
	@Operation(summary = "Obfuscates text in C")
	@GetMapping(value = "/c")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateC(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.C);
	}
	
	@Operation(summary = "Obfuscates text in C++")
	@GetMapping(value = "/cpp")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateCPP(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.C);
	}
	
	@Operation(summary = "Obfuscates text in C#")
	@GetMapping(value = "/csharp")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateCSharp(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.C_SHARP);
	}
	
	@Operation(summary = "Obfuscates text in Java")
	@GetMapping(value = "/java")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateJava(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.JAVA);
	}
	
	@Operation(summary = "Obfuscates text in JS")
	@GetMapping(value = "/javascript")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateJavaScript(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.JS);
	}
	
	@Operation(summary = "Obfuscates text in Python")
	@GetMapping(value = "/python")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscatePython(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.PYTHON);
	}
	
	@Operation(summary = "Obfuscates text in MASM 64 bit")
	@GetMapping(value = "/masm64")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateMasm64(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.MASM64);
	}
	
	@Operation(summary = "Obfuscates text in Bash")
	@GetMapping(value = "/bash")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscateBash(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.BASH);
	}
	
	@Operation(summary = "Obfuscates text in PowerShell")
	@GetMapping(value = "/powershell")
	@ResponseStatus(code = HttpStatus.OK)
	public String obfuscatePowerShell(
			@RequestParam(required = true, name = "text") String text,
			@RequestParam(required = false, name = "minOps", defaultValue = "5") int minOps,
			@RequestParam(required = false, name = "maxOps", defaultValue = "15") int maxOps) {
		return service.generate(text, minOps, maxOps, GenerationTarget.POWERSHELL);
	}
}
