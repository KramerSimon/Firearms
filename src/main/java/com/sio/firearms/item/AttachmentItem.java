package com.sio.firearms.item;

import com.sio.firearms.attachment.AttachmentType;
import net.minecraft.world.item.Item;

public class AttachmentItem extends Item {

    private final AttachmentType attachmentType;

    public AttachmentItem(Properties properties, AttachmentType attachmentType) {
        super(properties);
        this.attachmentType = attachmentType;
    }

    public AttachmentType getAttachmentType() {
        return attachmentType;
    }
}
