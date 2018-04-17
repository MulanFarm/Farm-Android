//package com.daweichang.vcfarm;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import com.xcc.mylibrary.widget.ShSwitchView;
//import com.zrgg.bxtxg.utils.GlideUtils;
//import com.zrgg.bxtxg.utils.ImgSelectConfig;
//import java.io.File;
//import java.util.List;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import cn.finalteam.galleryfinal.FunctionConfig;
//import cn.finalteam.galleryfinal.GalleryFinal;
//import cn.finalteam.galleryfinal.model.PhotoInfo;
//public class MainActivity extends BaseActivity {
//    @BindView(R.id.text)
//    TextView text;
//    @BindView(R.id.button)
//    Button button;
//    @BindView(R.id.switchView)
//    ShSwitchView switchView;
//    @BindView(R.id.image)
//    ImageView image;
//    private Bitmap bitmap;
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
//        switchView.setOnSwitchStateChangeListener(new ShSwitchView.OnSwitchStateChangeListener() {
//            public void onSwitchStateChange(ShSwitchView switchView, boolean isOn) {
//            }
//        });
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_test1);
//    }
//    private void initRefreshLayout() {
// //为BGARefreshLayout设置代理
//refreshLayout.setDelegate(this);
//        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
//        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
//        // 设置下拉刷新和上拉加载更多的风格
//        refreshLayout.setRefreshViewHolder(refreshViewHolder);
//        // 可选配置  -------------END
//        }
//public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
//        request.postRefresh();
//        }
//
//public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
//        if (request.hasNext()) {
//            request.postNext();
//            return true;
//        }
//        return false;
//        }
//    @OnClick(R.id.button)
//    public void onClick() {
//        text.setText("磁磁帅");
//        switchView.setOn(true);
////        PayTools payTools = new PayTools(this);
////        payTools.payStart();
////        AlipayPay pay = new AlipayPay(this);
////        pay.payStart();
////        bitmap = toturn(bitmap);
////        image.setImageBitmap(bitmap);
//
//        //取头像
//        FunctionConfig squareConfig = ImgSelectConfig.getSquareConfig(this, true, 300, 300);
//        GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, squareConfig, new GalleryFinal.OnHanlderResultCallback() {
//            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
//                int size = resultList.size();
//                if (size > 0) {
//                    PhotoInfo photoInfo = resultList.get(0);
//                    String photoPath = photoInfo.getPhotoPath();
//                    File file = new File(photoPath);
//                    GlideUtils.displayOfFile(MainActivity.this, image, file);
////                    upDateImg(file);
//                }
//            }
//            public void onHanlderFailure(int requestCode, String errorMsg) {
//            }
//        });
//    }
//    private static final int REQUEST_CODE_GALLERY = 200;
//    /**
//     * 图片旋转90度
//     * 顺时针方向
//     *
//     * @param img
//     * @return
//     */
//    public Bitmap toturn(Bitmap img) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90); /*旋转90度*/
//        int width = img.getWidth();
//        int height = img.getHeight();
//        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
//        return img;
//    }
//}