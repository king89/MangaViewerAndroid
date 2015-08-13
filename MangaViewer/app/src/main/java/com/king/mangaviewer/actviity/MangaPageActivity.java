package com.king.mangaviewer.actviity;

import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MyViewFlipper;

public class MangaPageActivity extends BaseActivity {

    MyViewFlipper vFlipper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.getAppViewModel().Manga.getSelectedMangaChapterItem().getTitle();
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub

        setContentView(R.layout.activity_manga_page);
        vFlipper = (MyViewFlipper) this.findViewById(R.id.viewFlipper);
        vFlipper.initial(getAppViewModel().Manga, getAppViewModel().Setting, handler, false);

    }

    @Override
    protected void update(Message msg) {
        this.getSupportActionBar().setTitle(getActionBarTitle());
    }

    @Override
    protected void goBack() {
        getAppViewModel().Manga.setNowPagePosition(0);
        getAppViewModel().Manga.setMangaPageList(null);
        super.goBack();
    }


    @Override
    protected boolean IsCanBack() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.page_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_setting) {

            View v = findViewById(R.id.menu_setting);
            displayPopupWindow(v);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.menu_page_setting, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
    }
}
