package com.arriwe.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arriwe.Model.Contact;
import com.sancsvision.arriwe.R;
import com.arriwe.utility.Constants;
import com.arriwe.utility.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Anand Jain on 01-11-2016.
 *
 */
public class SearchContactAdapter extends BaseAdapter {

    private LayoutInflater innflater;
    private Context context;
    private int res;
    private List<Contact> contactList = new ArrayList<>();
    private List<Contact> arrayList = new ArrayList<>();
    private String filter_text = "";


    public SearchContactAdapter(Context context, List<Contact> contactList, int res) {
        this.context = context;
        this.contactList = contactList;
        arrayList.addAll(contactList);
        this.res = res;
        innflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return contactList.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = innflater.inflate(res, null);
            holder = new Holder();
            holder.title = (TextView) view.findViewById(R.id.name);
            holder.detail = (TextView) view.findViewById(R.id.number);
            holder.profile_pic = (RoundedImageView) view.findViewById(R.id.profile_pic);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        if (!filter_text.equals("")) {
            if (contactList.get(i).getContactLocation().contains(filter_text)) {
                int startPos = contactList.get(i).getContactLocation().toLowerCase(Locale.getDefault()).indexOf(filter_text.toLowerCase(Locale.getDefault()));
                int endPos = startPos + filter_text.length();
                if (startPos != -1){
                   Spannable spannable = new SpannableString(contactList.get(i).getContactLocation());
                    ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLACK });
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.detail.setText(spannable);
                }
                else
                    holder.detail.setText(contactList.get(i).getContactLocation());
            } else {
                holder.detail.setText(contactList.get(i).getContactLocation());
            }
            if (contactList.get(i).getContactName().toLowerCase().contains(filter_text)) {
                int startPos = contactList.get(i).getContactName().toLowerCase(Locale.getDefault()).indexOf(filter_text.toLowerCase(Locale.getDefault()));
                int endPos = startPos + filter_text.length();
                if (startPos != -1){
                    Spannable spannable = new SpannableString(contactList.get(i).getContactName()+" "+"("+contactList.get(i).getContactNumber()+")");
                    ColorStateList blueColor = new ColorStateList(new int[][] { new int[] {}}, new int[] { Color.BLACK });
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.title.setText(csToUpperCase(spannable));
                }
                else
              holder.title.setText(Html.fromHtml("<strong>" + contactList.get(i).getContactName().toUpperCase() + "</strong>"+"" ));
// "+"("+contactList.get(i).getContactNumber()+")"));
            //} else {
                holder.title.setText(Html.fromHtml("<strong>" + contactList.get(i).getContactName().toUpperCase() + "</strong>"+" "));
                //"+"("+contactList.get(i).getContactNumber()+")")
            }
            }
         else {
            holder.title.setText(Html.fromHtml("<strong>" + contactList.get(i).getContactName().toUpperCase()));
            holder.detail.setText(contactList.get(i).getContactLocation()+ ""+" "+"   .    "+contactList.get(i).getContactNumber()+"");
        }
        String completeURl = Constants.DEV_IMG_BASE_URL+contactList.get(i).getContactPhoto();
        holder.profile_pic.setImageUrl(completeURl);
        return view;
    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        contactList.clear();
        if (charText.length() == 0) {
            contactList.addAll(arrayList);
            filter_text = "";
        } else {
            filter_text = charText;
            for (Contact wp : arrayList) {
                if (wp.getContactName().toLowerCase(Locale.getDefault())
                        .contains(charText) || wp.getContactLocation().toLowerCase(Locale.getDefault()).contains(charText)) {
                    contactList.add(wp);
                }

            }
        }
        notifyDataSetChanged();
    }
    private Spanned csToUpperCase(@NonNull Spanned s) {
        Object[] spans = s.getSpans(0,s.length(), Object.class);
        SpannableString spannableString = new SpannableString(s.toString().toUpperCase());

        // reapply the spans to the now uppercase string
        for (Object span : spans) {
            spannableString.setSpan(span,
                    s.getSpanStart(span),
                    s.getSpanEnd(span),
                    0);
        }

        return spannableString;
    }
    static class Holder {
        TextView title, detail;
        RoundedImageView profile_pic;
    }
}
