import os, shutil

os.makedirs('design/icons/svg', exist_ok=True)
shutil.copy('C:/Users/User/Downloads/house.svg', 'design/icons/svg/home.svg')
shutil.copy('C:/Users/User/Downloads/user.svg', 'design/icons/svg/user.svg')
shutil.copy('C:/Users/User/Downloads/users.svg', 'design/icons/svg/users.svg')
shutil.copy('C:/Users/User/Downloads/settings.svg', 'design/icons/svg/settings.svg')
shutil.copy('C:/Users/User/Downloads/log-out.svg', 'design/icons/svg/logout.svg')
shutil.copy('C:/Users/User/Downloads/search.svg', 'design/icons/svg/search.svg')

with open('design/icons/svg/lock.svg', 'w', encoding='utf-8') as f:
    f.write('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-lock"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>')

with open('design/icons/svg/arrow-left.svg', 'w', encoding='utf-8') as f:
    f.write('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-arrow-left"><path d="m12 19-7-7 7-7"/><path d="M19 12H5"/></svg>')

with open('design/icons/svg/eye.svg', 'w', encoding='utf-8') as f:
    f.write('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-eye"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/></svg>')

with open('design/icons/svg/eye-off.svg', 'w', encoding='utf-8') as f:
    f.write('<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-eye-off"><path d="M9.88 9.88a3 3 0 1 0 4.24 4.24"/><path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68"/><path d="M6.61 6.61A13.52 13.52 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61"/><line x1="2" x2="22" y1="2" y2="22"/></svg>')

print('SVG source directory prepared successfully.')
