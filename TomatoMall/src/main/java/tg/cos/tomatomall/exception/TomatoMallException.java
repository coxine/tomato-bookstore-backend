package tg.cos.tomatomall.exception;

public class TomatoMallException extends RuntimeException {
    public TomatoMallException(String message) {
        super(message);
    }

    public static Exception fileUploadFail() {
        return new TomatoMallException("文件上传错误!");
    }
}
