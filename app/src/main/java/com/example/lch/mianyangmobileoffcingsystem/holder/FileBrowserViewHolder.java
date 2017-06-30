package com.example.lch.mianyangmobileoffcingsystem.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lch.mianyangmobileoffcingsystem.R;
import com.example.lch.mianyangmobileoffcingsystem.adapter.FileBrowserAdapter;
import com.example.lch.mianyangmobileoffcingsystem.config.MyCache;
import com.netease.nim.uikit.common.adapter.TViewHolder;

import java.io.File;

/**
 * Created by lch on 2017/3/13.
 */

public class FileBrowserViewHolder extends TViewHolder {
    private ImageView fileImage;
    private TextView fileName;
    private FileBrowserAdapter.FileManagerItem fileItem;

    private Bitmap directoryBitmap;
    private Bitmap fileBitmap;

    @Override
    protected int getResId() {
        return R.layout.file_browser_list_item;
    }

    @Override
    protected void inflate() {
        directoryBitmap = BitmapFactory.decodeResource(MyCache.getContext().getResources(),R.mipmap.folder_icon);
        fileBitmap = BitmapFactory.decodeResource(MyCache.getContext().getResources(), R.mipmap.file_icon);
        fileImage = (ImageView) view.findViewById(R.id.file_image);
        fileName = (TextView) view.findViewById(R.id.file_name);
    }

    @Override
    protected void refresh(Object item) {
        fileItem = (FileBrowserAdapter.FileManagerItem) item;

        File f = new File(fileItem.getPath());
        if(fileItem.getName().equals("@1")) {
            fileName.setText("/");
            fileImage.setImageBitmap(directoryBitmap);
        } else if(fileItem.getName().equals("@2")) {
            fileName.setText("..");
            fileImage.setImageBitmap(directoryBitmap);
        } else {
            fileName.setText(fileItem.getName());
            if(f.isDirectory()) {
                fileImage.setImageBitmap(directoryBitmap);
            } else if (f.isFile()) {
                fileImage.setImageBitmap(fileBitmap);
            }
        }

    }
}
