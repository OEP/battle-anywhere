package org.oep.crawler.game;

import java.util.Random;
import java.util.Vector;

public class Attack {
	
	private static Random RNG = new Random();
	
	public enum Attribute { Strong, Quick, Smart };
	
	Attribute mAttack, mDefense;
	private Vector<Integer> mDice = new Vector<Integer>();
	private int mAmplifier, mDampener;
	private String mName;
	
	public Attack() {
		this(Attribute.Strong, Attribute.Quick);
	}
	
	public Attack(Attribute attack, Attribute defense) {
		this(null,attack,defense,null,1,1);
	}
	
	public Attack(String name, Attribute attack, Attribute defense, String dice, int amp, int dmp) {
		mName = name;
		mAttack = attack;
		mDefense = defense;
		addDice(dice);
		mAmplifier = amp;
		mDampener = dmp;
	}
	
	public Attribute getAttack() {
		return mAttack;
	}
	
	public Attribute getDefense() {
		return mDefense;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public int calculateDamage(int attack, int defense) {
		return Math.max( attack - defense + rollDice(), 1 ) * mAmplifier / mDampener;
	}
	
	public void addDice(String dice) {
		if(dice == null) return;
		
		dice = dice.toLowerCase();
		String split[] = dice.split("d");
		
		if(split.length != 2) throw new IllegalArgumentException("Format is: [dice]d[sides]");
		
		int n = Integer.parseInt(split[0]);
		int sides = Integer.parseInt(split[1]);
		
		addDice(n,sides);
	}
	
	public void addDie(int sides) {
		mDice.add(sides);
	}
	
	public void addDice(int n, int sides) {
		for(int i = 0; i < n; i++) {
			addDie(sides);
		}
	}
	
	public int rollDice() {
		if(mDice == null) return 0;
		
		int sum = 0;
		
		for(int i = 0; i < mDice.size(); i++) {
			int s = mDice.elementAt(i);
			sum += Math.signum(s) * Attack.RNG.nextInt( Math.abs(s) );
		}
		
		return sum;
	}
	
	public String toString() {
		return mName;
	}

	public void setAttackAttribute(String value) {
		mAttack = chooseAttribute(value);
	}

	public void setDefenseAttribute(String value) {
		mDefense = chooseAttribute(value);
	}

	public void setAmplifier(int amp) {
		mAmplifier = amp;
	}

	public void setDampener(int dmp) {
		mDampener = dmp;
	}
	
	private Attribute chooseAttribute(String att) {
		att = att.toLowerCase();
		
		if(att.compareTo(ATT_STRONG) == 0)
			return Attribute.Strong;
		else if(att.compareTo(ATT_QUICK) == 0)
			return Attribute.Quick;
		else if(att.compareTo(ATT_SMART) == 0)
			return Attribute.Smart;
		
		throw new IllegalArgumentException(String.format("Valid arguments are: '%s', '%s', and '%s'",
				ATT_STRONG, ATT_QUICK, ATT_SMART));
	}
	
	public static final String
		ATT_STRONG = "strong",
		ATT_QUICK = "quick",
		ATT_SMART = "smart";
}
