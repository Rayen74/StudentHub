import os, shutil

os.makedirs('design/icons/svg', exist_ok=True)
shutil.copy('C:/Users/User/Downloads/book-copy.svg', 'design/icons/svg/book-copy.svg')
shutil.copy('C:/Users/User/Downloads/clipboard-check.svg', 'design/icons/svg/clipboard-check.svg')

ic_matieres = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M5,7a2,2 0,0 0,-2 2v11"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M5.803,18H5a2,2 0,0 0,0 4h9.5a0.5,0.5 0,0 0,0.5 -0.5V21"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M9,15V4a2,2 0,0 1,2 -2h9.5a0.5,0.5 0,0 1,0.5 0.5v14a0.5,0.5 0,0 1,-0.5 0.5H11a2,2 0,0 1,0 -4h10"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>'''

ic_taches = '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:pathData="M9,2h6a1,1 0,0 1,1 1v2a1,1 0,0 1,-1 1H9a1,1 0,0 1,-1 -1V3a1,1 0,0 1,1 -1z"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M16,4h2a2,2 0,0 1,2 2v14a2,2 0,0 1,-2 2H6a2,2 0,0 1,-2 -2V6a2,2 0,0 1,2 -2h2"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
    <path
        android:pathData="M9,14l2,2l4,-4"
        android:strokeColor="#FF000000"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"/>
</vector>'''

with open('app/src/main/res/drawable/ic_matieres.xml', 'w', encoding='utf-8') as f:
    f.write(ic_matieres)

with open('app/src/main/res/drawable/ic_taches.xml', 'w', encoding='utf-8') as f:
    f.write(ic_taches)

print('New icons created successfully.')
