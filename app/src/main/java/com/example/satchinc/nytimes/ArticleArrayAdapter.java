package com.example.satchinc.nytimes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by satchinc on 6/21/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, R.layout.article_item_result, articles);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Article article = this.getItem(position);
        ViewHolder viewholder ;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.article_item_result, parent, false);
            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        }
        else{
            viewholder= (ViewHolder) convertView.getTag();
        }
        String thumbnail = article.getThumbnail();
        if(!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(viewholder.ivImage);
        }
        viewholder.tvTitle.setText(article.getHeadline());


        return convertView;
    }

    static class ViewHolder{
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
