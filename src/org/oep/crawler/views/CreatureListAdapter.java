package org.oep.crawler.views;

import java.util.List;

import org.oep.crawler.game.Creature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CreatureListAdapter extends BaseAdapter{
	private List<Creature> mCreatures;
	private LayoutInflater mInflater;
	private int mProfileField, mNameField, mItemLayout, mHPField, mLevelField;
	
	public CreatureListAdapter(Context ctx, List<Creature> creatures) {
		this(ctx,creatures,org.oep.battle.R.layout.list_creature_item, org.oep.battle.R.id.creature_profile,
				org.oep.battle.R.id.creature_name, org.oep.battle.R.id.creature_hp,
				org.oep.battle.R.id.creature_level);
	}
	
	public CreatureListAdapter(Context ctx, List<Creature> creatures,
			int itemLayout, int profileField, int nameField, int hpField, int levelField) {
		mCreatures = creatures;
		mProfileField = profileField;
		mItemLayout = itemLayout;
		mProfileField = profileField;
		mNameField = nameField;
		mHPField = hpField;
		mLevelField = levelField;
		
		mInflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return mCreatures.size();
	}

	@Override
	public Object getItem(int position) {
		return mCreatures.get(position);
	}

	@Override
	public long getItemId(int position) {
		Creature c = (Creature) getItem(position);
		return c.getSeed();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Creature c = (Creature) getItem(position);
		
		if(convertView == null) {
			convertView = mInflater.inflate(mItemLayout, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(mNameField);
			holder.profile = (ImageView) convertView.findViewById(mProfileField);
			holder.hp = (TextView) convertView.findViewById(mHPField);
			holder.level = (TextView) convertView.findViewById(mLevelField);
			
			
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(Long.toHexString(c.getSeed()));
		holder.profile.setImageResource(c.getImageId());
		holder.level.setText(Integer.toString(c.getLevel()));
		holder.hp.setText(Integer.toString(c.getHP()));
		
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView level;
		TextView hp;
		ImageView profile;
	}
}
