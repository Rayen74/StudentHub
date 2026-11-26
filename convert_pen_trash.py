import os, shutil

os.makedirs('design/icons/svg', exist_ok=True)
shutil.copy('C:/Users/User/Downloads/pen.svg', 'design/icons/svg/pen.svg')
shutil.copy('C:/Users/User/Downloads/trash.svg', 'design/icons/svg/trash.svg')
shutil.copy('C:/Users/User/Downloads/eye.svg', 'design/icons/svg/eye.svg')

ic_pen = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M21.174,6.812a1,1 0,0 0,-3.986 -3.987L3.842,16.174a2,2 0,0 0,-0.5 0.83l-1.321,4.352a0.5,0.5 0,0 0,0.623 0.622l4.353,-1.32a2,2 0,0 0,0.83 -0.497z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>'''

ic_trash = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M10,11v6"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M14,11v6"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M19,6v14a2,2 0,0 1,-2 2H7a2,2 0,0 1,-2 -2V6"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M3,6h18"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M8,6V4a2,2 0,0 1,2 -2h4a2,2 0,0 1,2 2v2"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>'''

ic_eye = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M2.062,12.348a1,1 0,0 1,0 -0.696 10.75,10.75 0,0 1,19.876 0 1,1 0,0 1,0 0.696 10.75,10.75 0,0 1,-19.876 0z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <circle
        android:cx="12"
        android:cy="12"
        android:r="3"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"/>
</vector>'''

with open('app/src/main/res/drawable/ic_pen.xml', 'w', encoding='utf-8') as f:
    f.write(ic_pen)

with open('app/src/main/res/drawable/ic_trash.xml', 'w', encoding='utf-8') as f:
    f.write(ic_trash)

with open('app/src/main/res/drawable/ic_eye.xml', 'w', encoding='utf-8') as f:
    f.write(ic_eye)

print('Pen, trash, and eye icons generated successfully.')
