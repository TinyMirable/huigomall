import os

def rename_and_replace(directory,old_name,new_name):
    '''raname'''
    for root, dirs , files in os.walk(directory):
        # Process files
        for filename in files:
            file_path = os.path.join(root,filename)
            try:
                with open(file_path,'r',encoding='utf-8') as f:
                    content = f.read()
                if old_name in content:
                    new_content = content.replace(old_name,new_name)
                    with open(file_path,'w',encoding='utf-8') as wf:
                        wf.write(new_content)
            except (UnicodeDecodeError,PermissionError) as e:
                print(f"跳过文件 {file_path} : {e}")
                continue
        # Procee dirs 
        for dir_name in dirs:
            dir_path = os.path.join(directory,dir_name)
            rename_and_replace(dir_path,old_name,new_name)
            if old_name == dir_name:
                new_path = os.path.join(directory,new_name)
                try:
                    os.rename(dir_path,new_path)
                    print(f"已重命名目录 {dir_path} -> {new_path}")
                except Exception as e:
                    print(f"重命名失败 {dir_name}: {e}")

rename_and_replace(".","huigo","huigo")
