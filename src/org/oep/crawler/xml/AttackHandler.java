package org.oep.crawler.xml;

import java.util.HashMap;
import java.util.Vector;

import org.oep.crawler.game.Attack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AttackHandler extends DefaultHandler {
	
	private HashMap<String, Vector<Attack>> mAttackSets;
	private String mSetName;
	private Vector<Attack> mWorkingSet;
	private int mLevel;
	
	@Override
	public void startDocument() throws SAXException {
		mAttackSets = new HashMap<String,Vector<Attack>>();
		mLevel = 0;
	}
	
	@Override
	public void endDocument() throws SAXException {
		
	}
	
	@Override
	public void startElement(String URI, String localName, String qualifiedName, Attributes atts)
		throws SAXException {
		
		localName = localName.toLowerCase();
		
		switch(mLevel) {
		case 0:
			if(localName.compareTo(TAG_ATTACKS) != 0) {
				throw new SAXException("Top-level tag must be 'attacks'");
			}
			mLevel++;
			break;
			
		case 1:
			if(localName.compareTo(TAG_SET) != 0) {
				throw new SAXException("Second-level tag must be 'set'");
			}
			
			StringBuffer setName = new StringBuffer();
			for(int i = 0; i < atts.getLength(); i++) {
				String att = atts.getLocalName(i).toLowerCase();
				if(att.compareTo(ATT_NAME) == 0) {
					setName.append(atts.getValue(i));
				}
			}
			
			if(setName.length() == 0) {
				throw new SAXException("'set' tags require attribute 'name'");
			}
			
			mWorkingSet = new Vector<Attack>();
			mAttackSets.put(setName.toString(), mWorkingSet);
			break;
			
		case 2:
			if(localName.compareTo(TAG_ATTACK) != 0) {
				throw new SAXException("Only 'attack' tags are allowed within a 'set' tag.");
			}
			
			Attack attack = new Attack();
			
			for(int i = 0; i < atts.getLength(); i++) {
				String name = atts.getLocalName(i).toLowerCase();
				String value = atts.getValue(i);
				
				if(name.compareTo(ATT_NAME) == 0) {
					attack.setName(value);
				}
				else if(name.compareTo(ATT_DICE) == 0) {
					attack.addDice(value);
				}
				else if(name.compareTo(ATT_ATK) == 0) {
					attack.setAttackAttribute(value);
				}
				else if(name.compareTo(ATT_DEF) == 0) {
					attack.setDefenseAttribute(value);
				}
				else if(name.compareTo(ATT_AMP) == 0) {
					attack.setAmplifier(Integer.parseInt(value));
				}
				else if(name.compareTo(ATT_DMP) == 0) {
					attack.setDampener(Integer.parseInt(value));
				}
			}
			
			mWorkingSet.add(attack);
			break;
			
		default:
			throw new SAXException("Invalid depth");
				
		}
		
	}
	
	@Override
	public void endElement(String URI, String localName, String qualifiedName)
		throws SAXException {
		
	}
	
	@Override
	public void characters(char ch[], int start, int length) {
		
	}
	
	public HashMap<String, Vector<Attack>> getAttacks() {
		return mAttackSets;
	}
	
	public static final String
		TAG_ATTACKS = "attacks",
		TAG_SET = "set",
		TAG_ATTACK = "attack",
		ATT_NAME = "name",
		ATT_DICE = "dice",
		ATT_LEVEL = "level",
		ATT_ATK = "attack",
		ATT_DEF = "defend",
		ATT_DMP = "dmp",
		ATT_AMP = "amp";

}
