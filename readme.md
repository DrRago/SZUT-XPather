 public static String getBreadCrumbPath(final TreeItem selected){
        return getBreadCrumbPath(selected, selected);
    }
 
    private static String getBreadCrumbPath(final TreeItem selected, final TreeItem current){
        if(current.getParent() != null){
            String result = getBreadCrumbPath(selected, current.getParent());
            return result + current.getValue().toString().trim() + (selected == current ? "" :  " > ");
        }
 
        return current.getValue().toString().trim() + " > ";
    }
 
 
    treeView.getSelectionModel().getSelectedItem()