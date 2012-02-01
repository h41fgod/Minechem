package net.minecraft.minechem;

import java.io.IOException;

import net.minecraft.src.mod_Minechem;

public class Util {
	
	public static String convertNumbersToSuperscript(String formula) {
		formula = formula.replaceAll("0", "\u2080");
		formula = formula.replaceAll("1", "\u2081");
		formula = formula.replaceAll("2", "\u2082");
		formula = formula.replaceAll("3", "\u2083");
		formula = formula.replaceAll("4", "\u2084");
		formula = formula.replaceAll("5", "\u2085");
		formula = formula.replaceAll("6", "\u2086");
		formula = formula.replaceAll("7", "\u2087");
		formula = formula.replaceAll("8", "\u2088");
		formula = formula.replaceAll("9", "\u2089");
		return formula;
	}
	
	public static String getFullChemicalName(String formula) {
		try {
			return mod_Minechem.findChemicalName(formula);
		} catch(IOException e) {
			return "Unknown";
		}
	}
}