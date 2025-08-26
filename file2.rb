  # note - more specific errors are handled in the sso_login method
rescue ActiveRecord::RecordInvalid, ActiveRecord::RecordNotSaved => e
  Rails.logger.warn("SSO invite redemption failed: #{e}")
  raise Invite::RedemptionFailed
end

def domain_redirect_allowed?(hostname)
  allowed_domains = SiteSetting.discourse_connect_allowed_redirect_domains
  return false if allowed_domains.blank?
  return true if allowed_domains.split("|").include?("*")

  allowed_domains.split("|").include?(hostname)
scope :unread_type, ->(user, type, limit = 30) { unread_types(user, [type], limit) }
scope :unread_types,
      ->(user, types, limit = 30) do
        where(user_id: user.id, read: false, notification_type: types)
          .visible
          .includes(:topic)
          .limit(limit)
      end
scope :prioritized,
      ->(deprioritized_types = []) do
        scope = order("notifications.high_priority AND NOT notifications.read DESC")

        if deprioritized_types.present?
          scope =
            scope.order(
              DB.sql_fragment(
                "NOT notifications.read AND notifications.notification_type NOT IN (?) DESC",
                deprioritized_types,
              ),
            )
        else
          scope = scope.order("NOT notifications.read DESC")
        end

        scope.order("notifications.created_at DESC")
      end

          upload_path = @params[:upload_path]
    tmp_upload_path = @params[:tmp_upload_path]
    identifier = @params[:identifier]
    filename = @params[:filename]
    tmp_directory = @params[:tmp_directory]

    # delete destination files
    begin
      File.delete(upload_path)
      File.delete(tmp_upload_path)

      # frozen_string_literal: true

module WildcardUrlChecker
  def self.check_url(url, url_to_check)
    return false if !valid_url?(url_to_check)

    escaped_url = Regexp.escape(url).sub("\\*", '\S*')
    url_regex = Regexp.new("\\A#{escaped_url}\\z", "i")

    url_to_check.match?(url_regex)
  end

  private

  def self.valid_url?(url)
    uri = URI.parse(url)
    uri&.scheme.present? && uri&.host.present?
  rescue URI::InvalidURIError
    false
  end
end
