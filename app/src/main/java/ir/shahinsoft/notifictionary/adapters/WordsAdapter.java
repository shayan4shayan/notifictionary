package ir.shahinsoft.notifictionary.adapters;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.shahinsoft.notifictionary.R;
import ir.shahinsoft.notifictionary.model.Category;
import ir.shahinsoft.notifictionary.model.Translate;

/**
 * Created by shayan on 9/29/2017.
 */

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private List<Translate> translates = new ArrayList<>();
    private PopupMenu menu;
    private Translate mCurrentTranslate;
    private List<Category> categories;
    private OnItemActionListener listener;

    public WordsAdapter(List<Category> categories, OnItemActionListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setTranslates(List<Translate> translates) {
        this.translates = translates;
    }

    public List<Translate> getTranslates() {
        return translates;
    }

    public void add(List<Translate> translates) {
        int oldSize = this.translates.size();
        this.translates.addAll(translates);
        int newSize = this.translates.size();
        notifyItemRangeInserted(oldSize, newSize);
    }

    public void updateCategoryColor(Category category) {
        for (Category cat : categories) {
            if (cat.getId() == category.getId()) {
                cat.setColor(category.getColor());
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final ViewHolder holder = new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false)
        );
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menu != null) {
                    menu.dismiss();
                }
                menu = new PopupMenu(parent.getContext(), holder.menu);
                mCurrentTranslate = translates.get(holder.getAdapterPosition());
                buildMenu();
                menu.show();
            }
        });
        holder.mSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.play(translates.get(holder.getAdapterPosition()).getName());
            }
        });
        return holder;
    }


    private void buildMenu() {
        if (menu == null) return;
        Log.d("WordsAdapter", "" + categories.size());
        menu.getMenuInflater().inflate(R.menu.menu_item, menu.getMenu());
        menu.setOnMenuItemClickListener(this);
        MenuItem item = menu.getMenu().findItem(R.id.menu_move);
        if (listener.canShowMoveMenu() || mCurrentTranslate.getCatId() == -1) {
            for (final Category c : categories) {
                MenuItem menu = item.getSubMenu().add(c.getName());
                menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        listener.onMove(mCurrentTranslate, c);
                        return true;
                    }
                });
            }
        } else {
            item.setVisible(false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Translate translate = translates.get(position);
        holder.mWordView.setText(translate.getName());
        holder.mTranslateView.setText(translate.getTranslate());
        holder.noCategory.setBackgroundColor(findCategoryById(translate.getCatId()).getColor());
    }

    private Category findCategoryById(int id) {
        for (Category c : categories) {
            if (c.getId() == id) {
                return c;
            }
        }
        return new Category("", -1);
    }

    @Override
    public int getItemCount() {
        return translates != null ? translates.size() : 0;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_remove:
                listener.onDeleted(mCurrentTranslate);
                break;
            case R.id.menu_edit:
                listener.onEdit(mCurrentTranslate);
                break;
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mWordView, mTranslateView;
        ImageView menu, noCategory, mSpeech;
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            mWordView = itemView.findViewById(R.id.text_word);
            mTranslateView = itemView.findViewById(R.id.text_translate);
            menu = itemView.findViewById(R.id.more);
            mSpeech = itemView.findViewById(R.id.btn_speech);
            noCategory = itemView.findViewById(R.id.no_category);
            cardView = itemView.findViewById(R.id.card);
        }
    }

    public interface OnItemActionListener {
        void onDeleted(Translate translate);

        void onEdit(Translate translate);

        void play(String name);

        void onMove(Translate translate, Category category);

        boolean canShowMoveMenu();
    }
}
